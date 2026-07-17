package br.com.sicredi.vote.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import br.com.sicredi.vote.converter.VotingSessionConverter;
import br.com.sicredi.vote.dto.VotingSessionRequestDTO;
import br.com.sicredi.vote.dto.VotingSessionResponseDTO;
import br.com.sicredi.vote.exception.AppError;
import br.com.sicredi.vote.exception.BusinessException;
import br.com.sicredi.vote.model.Agenda;
import br.com.sicredi.vote.model.AgendaStatus;
import br.com.sicredi.vote.model.VoteChoice;
import br.com.sicredi.vote.model.VotingSession;
import br.com.sicredi.vote.model.VotingSessionStatus;
import br.com.sicredi.vote.repository.AgendaRepository;
import br.com.sicredi.vote.repository.VoteRepository;
import br.com.sicredi.vote.repository.VotingSessionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class VotingSessionService {

    private VotingSessionRepository repository;
    private VotingSessionConverter converter;
    private AgendaRepository agendaRepository;
    private VoteRepository voteRepository;

    private static final int DEFAULT_SESSION_CLOSE_IN_MINUTES = 1;

    public VotingSessionResponseDTO createVotingSession(VotingSessionRequestDTO request) throws BusinessException {
        VotingSession votingSession = converter.convertFromDto(request);
        validateVotingSession(votingSession, request);

        Agenda agenda = resolveAgenda(request.getAgendaId());
        votingSession.setAgenda(agenda);

        if (votingSession.getClosesAt() == null) {
            votingSession.setClosesAt(votingSession.getOpenedAt().plusMinutes(DEFAULT_SESSION_CLOSE_IN_MINUTES));
        }

        votingSession.setStatus(VotingSessionStatus.OPEN);

        votingSession = repository.save(votingSession);

        return converter.convertToDto(votingSession);
    }

    public VotingSessionResponseDTO getVotingSession(UUID id) throws BusinessException {
        VotingSession votingSession = repository.findById(id)
                .orElseThrow(() -> {
                    log.error(AppError.VOTING_SESSION_NOT_FOUND.getMessage(), id);
                    return new BusinessException(AppError.VOTING_SESSION_NOT_FOUND, String.valueOf(id));
                });

        return converter.convertToDto(votingSession);
    }

    public List<VotingSessionResponseDTO> listVotingSessions() {
        List<VotingSession> votingSessions = repository.findAllByOrderByCreatedAtDesc();

        if (CollectionUtils.isEmpty(votingSessions)) {
            return Collections.emptyList();
        }

        return votingSessions.stream().map(converter::convertToDto).toList();
    }

    /**
     * Computes all the Voting Session
     * that are still OPEN and closeAt < now
     */
    @Transactional
    public void computeVotingSessionsResult() {
        List<VotingSession> sessionsToClose =
                repository.findByStatusAndClosesAtBefore(VotingSessionStatus.OPEN, LocalDateTime.now(ZoneId.systemDefault()));

        sessionsToClose.forEach(votingSession -> {
            log.info("Computing results for voting session {}", votingSession.getId());

            long yesVotes = voteRepository.countByVotingSessionIdAndChoice(votingSession.getId(), VoteChoice.YES);
            long noVotes = voteRepository.countByVotingSessionIdAndChoice(votingSession.getId(), VoteChoice.NO);

            //If result is a draw, automatically rejects Agenda
            AgendaStatus agendaStatus = yesVotes > noVotes ? AgendaStatus.APPROVED : AgendaStatus.REJECTED;
            Agenda agenda = votingSession.getAgenda();
            agenda.setStatus(agendaStatus);
            agendaRepository.save(agenda);

            votingSession.setStatus(VotingSessionStatus.CLOSED);
            repository.save(votingSession);
        });
    }

    private Agenda resolveAgenda(UUID agendaId) throws BusinessException {
        return agendaRepository.findById(agendaId)
                .orElseThrow(() -> {
                    log.error(AppError.AGENDA_NOT_FOUND.getMessage(), agendaId);
                    return new BusinessException(AppError.AGENDA_NOT_FOUND, String.valueOf(agendaId));
                });
    }

    private void validateVotingSession(VotingSession votingSession, VotingSessionRequestDTO request)
            throws BusinessException {
        if (votingSession == null) {
            log.error(AppError.NULL_VOTING_SESSION.getMessage());
            throw new BusinessException(AppError.NULL_VOTING_SESSION);
        }

        if (request.getAgendaId() == null) {
            log.error(AppError.NULL_AGENDA_ID.getMessage());
            throw new BusinessException(AppError.NULL_AGENDA_ID);
        }

        if (votingSession.getOpenedAt() == null || !votingSession.getOpenedAt().isAfter(LocalDateTime.now(ZoneId.systemDefault()))) {
            log.error(AppError.INVALID_OPEN_DATE.getMessage(), votingSession.getOpenedAt());
            throw new BusinessException(AppError.INVALID_OPEN_DATE, String.valueOf(votingSession.getOpenedAt()));
        }

        if (repository.findByAgendaId(request.getAgendaId()).isPresent()) {
            log.error(AppError.SESSION_ALREADY_EXISTS.getMessage(), request.getAgendaId());
            throw new BusinessException(AppError.SESSION_ALREADY_EXISTS, String.valueOf(request.getAgendaId()));
        }
    }
}
