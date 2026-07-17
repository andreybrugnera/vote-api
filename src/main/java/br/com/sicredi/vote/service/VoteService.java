package br.com.sicredi.vote.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.sicredi.vote.converter.VoteConverter;
import br.com.sicredi.vote.dto.VoteRequestDTO;
import br.com.sicredi.vote.dto.VoteResponseDTO;
import br.com.sicredi.vote.exception.AppError;
import br.com.sicredi.vote.exception.BusinessException;
import br.com.sicredi.vote.model.Member;
import br.com.sicredi.vote.model.Vote;
import br.com.sicredi.vote.model.VotingSession;
import br.com.sicredi.vote.repository.MemberRepository;
import br.com.sicredi.vote.repository.VoteRepository;
import br.com.sicredi.vote.repository.VotingSessionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class VoteService {

    private VoteRepository repository;
    private VoteConverter converter;
    private MemberRepository memberRepository;
    private VotingSessionRepository votingSessionRepository;

    public VoteResponseDTO createVote(VoteRequestDTO request) throws BusinessException {
        Vote vote = converter.convertFromDto(request);
        validateVote(vote, request);

        Member member = resolveMember(request.getMemberId());
        VotingSession votingSession = resolveVotingSession(request.getVotingSessionId());

        validateVotingWindow(votingSession);
        validateMemberHasNotVoted(votingSession.getId(), member.getId());

        vote.setMember(member);
        vote.setVotingSession(votingSession);

        vote = repository.save(vote);
        log.info("Vote [{}] registered for member [{}] on voting session [{}].",
                vote.getId(), member.getId(), votingSession.getId());

        return converter.convertToDto(vote);
    }

    private Member resolveMember(UUID memberId) throws BusinessException {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error(AppError.MEMBER_NOT_FOUND.getMessage(), memberId);
                    return new BusinessException(AppError.MEMBER_NOT_FOUND, String.valueOf(memberId));
                });
    }

    private VotingSession resolveVotingSession(UUID votingSessionId) throws BusinessException {
        return votingSessionRepository.findById(votingSessionId)
                .orElseThrow(() -> {
                    log.error(AppError.VOTING_SESSION_NOT_FOUND.getMessage(), votingSessionId);
                    return new BusinessException(AppError.VOTING_SESSION_NOT_FOUND, String.valueOf(votingSessionId));
                });
    }

    private void validateVote(Vote vote, VoteRequestDTO request) throws BusinessException {
        if (vote == null) {
            log.error(AppError.NULL_VOTE.getMessage());
            throw new BusinessException(AppError.NULL_VOTE);
        }

        if (request.getMemberId() == null) {
            log.error(AppError.NULL_VOTE_MEMBER_ID.getMessage());
            throw new BusinessException(AppError.NULL_VOTE_MEMBER_ID);
        }

        if (request.getVotingSessionId() == null) {
            log.error(AppError.NULL_VOTE_SESSION_ID.getMessage());
            throw new BusinessException(AppError.NULL_VOTE_SESSION_ID);
        }

        if (vote.getChoice() == null) {
            log.error(AppError.NULL_VOTE_CHOICE.getMessage());
            throw new BusinessException(AppError.NULL_VOTE_CHOICE);
        }
    }

    private void validateVotingWindow(VotingSession votingSession) throws BusinessException {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());

        if (now.isBefore(votingSession.getOpenedAt())) {
            log.error(AppError.VOTING_SESSION_NOT_OPEN.getMessage());
            throw new BusinessException(AppError.VOTING_SESSION_NOT_OPEN);
        }

        if (now.isAfter(votingSession.getClosesAt())) {
            log.error(AppError.VOTING_SESSION_CLOSED.getMessage());
            throw new BusinessException(AppError.VOTING_SESSION_CLOSED);
        }
    }

    private void validateMemberHasNotVoted(UUID votingSessionId, UUID memberId) throws BusinessException {
        if (repository.existsByVotingSessionIdAndMemberId(votingSessionId, memberId)) {
            log.error(AppError.MEMBER_ALREADY_VOTED.getMessage(), memberId, votingSessionId);
            throw new BusinessException(AppError.MEMBER_ALREADY_VOTED,
                    String.valueOf(memberId), String.valueOf(votingSessionId));
        }
    }
}
