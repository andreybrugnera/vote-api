package br.com.sicredi.vote.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class VotingSessionServiceTest {

    @Mock
    private VotingSessionRepository repository;

    @Mock
    private VotingSessionConverter converter;

    @Mock
    private AgendaRepository agendaRepository;

    @Mock
    private VoteRepository voteRepository;

    @InjectMocks
    private VotingSessionService service;

    @Test
    void createsVotingSessionWhenRequestIsValid() throws BusinessException {
        UUID agendaId = UUID.randomUUID();
        LocalDateTime openedAt = LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(5);
        VotingSessionRequestDTO request = new VotingSessionRequestDTO(agendaId, openedAt, openedAt.plusMinutes(10));
        VotingSession converted = votingSession(null, openedAt, openedAt.plusMinutes(10));
        Agenda agenda = Agenda.builder().id(agendaId).build();
        VotingSession saved = votingSession(UUID.randomUUID(), openedAt, openedAt.plusMinutes(10));
        VotingSessionResponseDTO expectedResponse = VotingSessionResponseDTO.builder().id(saved.getId()).build();

        when(converter.convertFromDto(request)).thenReturn(converted);
        when(repository.findByAgendaId(agendaId)).thenReturn(Optional.empty());
        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agenda));
        when(repository.save(converted)).thenReturn(saved);
        when(converter.convertToDto(saved)).thenReturn(expectedResponse);

        VotingSessionResponseDTO response = service.createVotingSession(request);

        assertThat(response).isSameAs(expectedResponse);
        assertThat(converted.getAgenda()).isSameAs(agenda);
        verify(repository).save(converted);
    }

    @Test
    void defaultsClosesAtToOneMinuteAfterOpenedAt() throws BusinessException {
        UUID agendaId = UUID.randomUUID();
        LocalDateTime openedAt = LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(5);
        VotingSessionRequestDTO request = new VotingSessionRequestDTO(agendaId, openedAt, null);
        VotingSession converted = votingSession(null, openedAt, null);

        when(converter.convertFromDto(request)).thenReturn(converted);
        when(repository.findByAgendaId(agendaId)).thenReturn(Optional.empty());
        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(Agenda.builder().id(agendaId).build()));
        when(repository.save(any())).thenReturn(converted);
        when(converter.convertToDto(any())).thenReturn(VotingSessionResponseDTO.builder().build());

        service.createVotingSession(request);

        ArgumentCaptor<VotingSession> captor = ArgumentCaptor.forClass(VotingSession.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getClosesAt()).isEqualTo(openedAt.plusMinutes(1));
    }

    @Test
    void rejectsVotingSessionThatConvertsToNull() {
        when(converter.convertFromDto(null)).thenReturn(null);

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createVotingSession(null))
                .withMessage(AppError.NULL_VOTING_SESSION.getMessage())
                .extracting(BusinessException::getErrorCode)
                .isEqualTo(AppError.NULL_VOTING_SESSION.getCode());

        verify(repository, never()).save(any());
    }

    @Test
    void rejectsVotingSessionWithoutAgendaId() {
        LocalDateTime openedAt = LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(5);
        VotingSessionRequestDTO request = new VotingSessionRequestDTO(null, openedAt, null);
        when(converter.convertFromDto(request)).thenReturn(votingSession(null, openedAt, null));

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createVotingSession(request))
                .withMessage(AppError.NULL_AGENDA_ID.getMessage())
                .extracting(BusinessException::getErrorCode)
                .isEqualTo(AppError.NULL_AGENDA_ID.getCode());

        verify(repository, never()).save(any());
    }

    @Test
    void rejectsVotingSessionWithOpenDateInThePast() {
        UUID agendaId = UUID.randomUUID();
        LocalDateTime openedAt = LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(1);
        VotingSessionRequestDTO request = new VotingSessionRequestDTO(agendaId, openedAt, null);
        when(converter.convertFromDto(request)).thenReturn(votingSession(null, openedAt, null));

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createVotingSession(request))
                .extracting(BusinessException::getErrorCode)
                .isEqualTo(AppError.INVALID_OPEN_DATE.getCode());

        verify(repository, never()).save(any());
    }

    @Test
    void rejectsVotingSessionWhenSessionAlreadyExistsForAgenda() {
        UUID agendaId = UUID.randomUUID();
        LocalDateTime openedAt = LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(5);
        VotingSessionRequestDTO request = new VotingSessionRequestDTO(agendaId, openedAt, null);

        when(converter.convertFromDto(request)).thenReturn(votingSession(null, openedAt, null));
        when(repository.findByAgendaId(agendaId)).thenReturn(Optional.of(votingSession(UUID.randomUUID(), openedAt, null)));

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createVotingSession(request))
                .withMessage("A voting session already exists for agenda [%s].", agendaId)
                .extracting(BusinessException::getErrorCode)
                .isEqualTo(AppError.SESSION_ALREADY_EXISTS.getCode());

        verify(repository, never()).save(any());
    }

    @Test
    void rejectsVotingSessionWhenAgendaDoesNotExist() {
        UUID agendaId = UUID.randomUUID();
        LocalDateTime openedAt = LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(5);
        VotingSessionRequestDTO request = new VotingSessionRequestDTO(agendaId, openedAt, null);

        when(converter.convertFromDto(request)).thenReturn(votingSession(null, openedAt, null));
        when(repository.findByAgendaId(agendaId)).thenReturn(Optional.empty());
        when(agendaRepository.findById(agendaId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createVotingSession(request))
                .withMessage("Agenda with id [%s] was not found.", agendaId)
                .extracting(BusinessException::getErrorCode)
                .isEqualTo(AppError.AGENDA_NOT_FOUND.getCode());

        verify(repository, never()).save(any());
    }

    @Test
    void getsVotingSessionById() throws BusinessException {
        VotingSession votingSession = votingSession(UUID.randomUUID(), LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(5), null);
        VotingSessionResponseDTO expectedResponse = VotingSessionResponseDTO.builder().id(votingSession.getId()).build();

        when(repository.findById(votingSession.getId())).thenReturn(Optional.of(votingSession));
        when(converter.convertToDto(votingSession)).thenReturn(expectedResponse);

        VotingSessionResponseDTO response = service.getVotingSession(votingSession.getId());

        assertThat(response).isSameAs(expectedResponse);
    }

    @Test
    void rejectsGetWhenVotingSessionDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.getVotingSession(id))
                .withMessage("Voting session with id [%s] was not found.", id)
                .extracting(BusinessException::getErrorCode)
                .isEqualTo(AppError.VOTING_SESSION_NOT_FOUND.getCode());
    }

    @Test
    void listsRegisteredVotingSessions() {
        VotingSession first = votingSession(UUID.randomUUID(), LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(5), null);
        VotingSession second = votingSession(UUID.randomUUID(), LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(6), null);
        VotingSessionResponseDTO firstResponse = VotingSessionResponseDTO.builder().id(first.getId()).build();
        VotingSessionResponseDTO secondResponse = VotingSessionResponseDTO.builder().id(second.getId()).build();

        when(repository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(first, second));
        when(converter.convertToDto(first)).thenReturn(firstResponse);
        when(converter.convertToDto(second)).thenReturn(secondResponse);

        assertThat(service.listVotingSessions()).containsExactly(firstResponse, secondResponse);
    }

    @Test
    void listsEmptyWhenThereAreNoVotingSessions() {
        when(repository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());
        assertThat(service.listVotingSessions()).isEmpty();
    }

    @Test
    void approvesAgendaWhenYesGreaterThanNoVotes() {
        Agenda agenda = Agenda.builder().id(UUID.randomUUID()).build();
        VotingSession session = closedSession(agenda);

        when(repository.findByStatusAndClosesAtBefore(eq(VotingSessionStatus.OPEN), any()))
                .thenReturn(List.of(session));
        when(voteRepository.countByVotingSessionIdAndChoice(session.getId(), VoteChoice.YES)).thenReturn(3L);
        when(voteRepository.countByVotingSessionIdAndChoice(session.getId(), VoteChoice.NO)).thenReturn(1L);

        service.computeVotingSessionsResult();

        assertThat(agenda.getStatus()).isEqualTo(AgendaStatus.APPROVED);
        assertThat(session.getStatus()).isEqualTo(VotingSessionStatus.CLOSED);
        verify(agendaRepository).save(agenda);
        verify(repository).save(session);
    }

    @Test
    void rejectsAgendaWhenNoVotesGreaterThanYesVotes() {
        Agenda agenda = Agenda.builder().id(UUID.randomUUID()).build();
        VotingSession session = closedSession(agenda);

        when(repository.findByStatusAndClosesAtBefore(eq(VotingSessionStatus.OPEN), any()))
                .thenReturn(List.of(session));
        when(voteRepository.countByVotingSessionIdAndChoice(session.getId(), VoteChoice.YES)).thenReturn(1L);
        when(voteRepository.countByVotingSessionIdAndChoice(session.getId(), VoteChoice.NO)).thenReturn(2L);

        service.computeVotingSessionsResult();

        assertThat(agenda.getStatus()).isEqualTo(AgendaStatus.REJECTED);
        assertThat(session.getStatus()).isEqualTo(VotingSessionStatus.CLOSED);
        verify(agendaRepository).save(agenda);
        verify(repository).save(session);
    }

    @Test
    void rejectsAgendaWhenThereAreNoVotes() {
        Agenda agenda = Agenda.builder().id(UUID.randomUUID()).build();
        VotingSession session = closedSession(agenda);

        when(repository.findByStatusAndClosesAtBefore(eq(VotingSessionStatus.OPEN), any()))
                .thenReturn(List.of(session));
        when(voteRepository.countByVotingSessionIdAndChoice(session.getId(), VoteChoice.YES)).thenReturn(0L);
        when(voteRepository.countByVotingSessionIdAndChoice(session.getId(), VoteChoice.NO)).thenReturn(0L);

        service.computeVotingSessionsResult();

        assertThat(agenda.getStatus()).isEqualTo(AgendaStatus.REJECTED);
        assertThat(session.getStatus()).isEqualTo(VotingSessionStatus.CLOSED);
    }

    @Test
    void rejectsAgendaWhenResultIsDraw() {
        Agenda agenda = Agenda.builder().id(UUID.randomUUID()).build();
        VotingSession session = closedSession(agenda);

        when(repository.findByStatusAndClosesAtBefore(eq(VotingSessionStatus.OPEN), any()))
                .thenReturn(List.of(session));
        when(voteRepository.countByVotingSessionIdAndChoice(session.getId(), VoteChoice.YES)).thenReturn(2L);
        when(voteRepository.countByVotingSessionIdAndChoice(session.getId(), VoteChoice.NO)).thenReturn(2L);

        service.computeVotingSessionsResult();

        assertThat(agenda.getStatus()).isEqualTo(AgendaStatus.REJECTED);
        assertThat(session.getStatus()).isEqualTo(VotingSessionStatus.CLOSED);
    }

    @Test
    void computesNothingWhenThereAreNoClosedOpenSessions() {
        when(repository.findByStatusAndClosesAtBefore(eq(VotingSessionStatus.OPEN), any()))
                .thenReturn(List.of());

        service.computeVotingSessionsResult();

        verify(agendaRepository, never()).save(any());
        verify(repository, never()).save(any());
    }

    private VotingSession closedSession(Agenda agenda) {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        return VotingSession.builder()
                .id(UUID.randomUUID())
                .agenda(agenda)
                .openedAt(now.minusMinutes(10))
                .closesAt(now.minusMinutes(1))
                .status(VotingSessionStatus.OPEN)
                .createdAt(now.minusMinutes(10))
                .build();
    }

    private VotingSession votingSession(UUID id, LocalDateTime openedAt, LocalDateTime closesAt) {
        return VotingSession.builder()
                .id(id)
                .openedAt(openedAt)
                .closesAt(closesAt)
                .createdAt(LocalDateTime.now(ZoneId.systemDefault()))
                .build();
    }
}
