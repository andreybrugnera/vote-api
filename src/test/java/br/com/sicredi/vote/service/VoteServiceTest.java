package br.com.sicredi.vote.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.sicredi.vote.converter.VoteConverter;
import br.com.sicredi.vote.dto.VoteRequestDTO;
import br.com.sicredi.vote.dto.VoteResponseDTO;
import br.com.sicredi.vote.exception.AppError;
import br.com.sicredi.vote.exception.BusinessException;
import br.com.sicredi.vote.model.Member;
import br.com.sicredi.vote.model.Vote;
import br.com.sicredi.vote.model.VoteChoice;
import br.com.sicredi.vote.model.VotingSession;
import br.com.sicredi.vote.repository.MemberRepository;
import br.com.sicredi.vote.repository.VoteRepository;
import br.com.sicredi.vote.repository.VotingSessionRepository;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoteRepository repository;

    @Mock
    private VoteConverter converter;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private VotingSessionRepository votingSessionRepository;

    @InjectMocks
    private VoteService service;

    @Test
    void createsVoteWhenSessionIsOpen() throws BusinessException {
        UUID memberId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        VoteRequestDTO request = new VoteRequestDTO(sessionId, memberId, VoteChoice.YES);
        Vote converted = vote(null, VoteChoice.YES);
        Member member = Member.builder().id(memberId).build();
        VotingSession session = openSession(sessionId);
        Vote saved = vote(UUID.randomUUID(), VoteChoice.YES);
        VoteResponseDTO expectedResponse = VoteResponseDTO.builder().id(saved.getId()).build();

        when(converter.convertFromDto(request)).thenReturn(converted);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(votingSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(repository.existsByVotingSessionIdAndMemberId(sessionId, memberId)).thenReturn(false);
        when(repository.save(converted)).thenReturn(saved);
        when(converter.convertToDto(saved)).thenReturn(expectedResponse);

        VoteResponseDTO response = service.createVote(request);

        assertThat(response).isSameAs(expectedResponse);
        assertThat(converted.getMember()).isSameAs(member);
        assertThat(converted.getVotingSession()).isSameAs(session);
        verify(repository).save(converted);
    }

    @Test
    void rejectsVoteThatConvertsToNull() {
        when(converter.convertFromDto(null)).thenReturn(null);

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createVote(null))
                .withMessage(AppError.NULL_VOTE.getMessage())
                .extracting(BusinessException::getErrorCode)
                .isEqualTo(AppError.NULL_VOTE.getCode());

        verify(repository, never()).save(any());
    }

    @Test
    void rejectsVoteWithoutMemberId() {
        VoteRequestDTO request = new VoteRequestDTO(UUID.randomUUID(), null, VoteChoice.YES);
        when(converter.convertFromDto(request)).thenReturn(vote(null, VoteChoice.YES));

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createVote(request))
                .withMessage(AppError.NULL_VOTE_MEMBER_ID.getMessage())
                .extracting(BusinessException::getErrorCode)
                .isEqualTo(AppError.NULL_VOTE_MEMBER_ID.getCode());

        verify(repository, never()).save(any());
    }

    @Test
    void rejectsVoteWithoutSessionId() {
        VoteRequestDTO request = new VoteRequestDTO(null, UUID.randomUUID(), VoteChoice.YES);
        when(converter.convertFromDto(request)).thenReturn(vote(null, VoteChoice.YES));

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createVote(request))
                .withMessage(AppError.NULL_VOTE_SESSION_ID.getMessage())
                .extracting(BusinessException::getErrorCode)
                .isEqualTo(AppError.NULL_VOTE_SESSION_ID.getCode());

        verify(repository, never()).save(any());
    }

    @Test
    void rejectsVoteWithoutChoice() {
        VoteRequestDTO request = new VoteRequestDTO(UUID.randomUUID(), UUID.randomUUID(), null);
        when(converter.convertFromDto(request)).thenReturn(vote(null, null));

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createVote(request))
                .withMessage(AppError.NULL_VOTE_CHOICE.getMessage())
                .extracting(BusinessException::getErrorCode)
                .isEqualTo(AppError.NULL_VOTE_CHOICE.getCode());

        verify(repository, never()).save(any());
    }

    @Test
    void rejectsVoteWhenMemberDoesNotExist() {
        UUID memberId = UUID.randomUUID();
        VoteRequestDTO request = new VoteRequestDTO(UUID.randomUUID(), memberId, VoteChoice.YES);

        when(converter.convertFromDto(request)).thenReturn(vote(null, VoteChoice.YES));
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createVote(request))
                .withMessage("Member with id [%s] was not found.", memberId)
                .extracting(BusinessException::getErrorCode)
                .isEqualTo(AppError.MEMBER_NOT_FOUND.getCode());

        verify(repository, never()).save(any());
    }

    @Test
    void rejectsVoteWhenVotingSessionDoesNotExist() {
        UUID memberId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        VoteRequestDTO request = new VoteRequestDTO(sessionId, memberId, VoteChoice.YES);

        when(converter.convertFromDto(request)).thenReturn(vote(null, VoteChoice.YES));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(Member.builder().id(memberId).build()));
        when(votingSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createVote(request))
                .withMessage("Voting session with id [%s] was not found.", sessionId)
                .extracting(BusinessException::getErrorCode)
                .isEqualTo(AppError.VOTING_SESSION_NOT_FOUND.getCode());

        verify(repository, never()).save(any());
    }

    @Test
    void rejectsVoteWhenSessionIsNotOpenYet() {
        UUID memberId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        VoteRequestDTO request = new VoteRequestDTO(sessionId, memberId, VoteChoice.YES);
        VotingSession session = session(sessionId,
                LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(5), LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(10));

        when(converter.convertFromDto(request)).thenReturn(vote(null, VoteChoice.YES));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(Member.builder().id(memberId).build()));
        when(votingSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createVote(request))
                .withMessage(AppError.VOTING_SESSION_NOT_OPEN.getMessage())
                .extracting(BusinessException::getErrorCode)
                .isEqualTo(AppError.VOTING_SESSION_NOT_OPEN.getCode());

        verify(repository, never()).save(any());
    }

    @Test
    void rejectsVoteWhenSessionIsAlreadyClosed() {
        UUID memberId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        VoteRequestDTO request = new VoteRequestDTO(sessionId, memberId, VoteChoice.YES);
        VotingSession session = session(sessionId,
                LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(10), LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(5));

        when(converter.convertFromDto(request)).thenReturn(vote(null, VoteChoice.YES));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(Member.builder().id(memberId).build()));
        when(votingSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createVote(request))
                .withMessage(AppError.VOTING_SESSION_CLOSED.getMessage())
                .extracting(BusinessException::getErrorCode)
                .isEqualTo(AppError.VOTING_SESSION_CLOSED.getCode());

        verify(repository, never()).save(any());
    }

    @Test
    void rejectsVoteWhenMemberHasAlreadyVoted() {
        UUID memberId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        VoteRequestDTO request = new VoteRequestDTO(sessionId, memberId, VoteChoice.YES);

        when(converter.convertFromDto(request)).thenReturn(vote(null, VoteChoice.YES));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(Member.builder().id(memberId).build()));
        when(votingSessionRepository.findById(sessionId)).thenReturn(Optional.of(openSession(sessionId)));
        when(repository.existsByVotingSessionIdAndMemberId(sessionId, memberId)).thenReturn(true);

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createVote(request))
                .withMessage("Member [%s] has already voted on voting session [%s].", memberId, sessionId)
                .extracting(BusinessException::getErrorCode)
                .isEqualTo(AppError.MEMBER_ALREADY_VOTED.getCode());

        verify(repository, never()).save(any());
    }

    private Vote vote(UUID id, VoteChoice choice) {
        return Vote.builder()
                .id(id)
                .choice(choice)
                .createdAt(LocalDateTime.now(ZoneId.systemDefault()))
                .build();
    }

    private VotingSession openSession(UUID id) {
        return session(id, LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(1), LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(1));
    }

    private VotingSession session(UUID id, LocalDateTime openedAt, LocalDateTime closesAt) {
        return VotingSession.builder()
                .id(id)
                .openedAt(openedAt)
                .closesAt(closesAt)
                .createdAt(LocalDateTime.now(ZoneId.systemDefault()))
                .build();
    }
}
