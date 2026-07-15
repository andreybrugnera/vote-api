package br.com.sicredi.vote.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.sicredi.vote.dto.VoteRequestDTO;
import br.com.sicredi.vote.dto.VoteResponseDTO;
import br.com.sicredi.vote.model.Member;
import br.com.sicredi.vote.model.Vote;
import br.com.sicredi.vote.model.VoteChoice;
import br.com.sicredi.vote.model.VotingSession;

class VoteConverterTest {

    private final VoteConverter converter = new VoteConverter();

    @Test
    void convertsVoteToResponseDto() {
        UUID votingSessionId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        Vote vote = Vote.builder()
                .id(UUID.randomUUID())
                .votingSession(VotingSession.builder().id(votingSessionId).build())
                .member(Member.builder().id(memberId).build())
                .choice(VoteChoice.YES)
                .createdAt(LocalDateTime.now())
                .build();

        VoteResponseDTO response = converter.convertToDto(vote);

        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo(VoteConverter.VOTE_REGISTERED_MESSAGE);
        assertThat(response.getId()).isEqualTo(vote.getId());
        assertThat(response.getVotingSessionId()).isEqualTo(votingSessionId);
        assertThat(response.getMemberId()).isEqualTo(memberId);
        assertThat(response.getChoice()).isEqualTo(VoteChoice.YES);
        assertThat(response.getCreatedAt()).isEqualTo(vote.getCreatedAt());
    }

    @Test
    void returnsNullResponseWhenVoteIsNull() {
        assertThat(converter.convertToDto(null)).isNull();
    }

    @Test
    void convertsRequestDtoToVote() {
        VoteRequestDTO request = new VoteRequestDTO(UUID.randomUUID(), UUID.randomUUID(), VoteChoice.NO);

        Vote vote = converter.convertFromDto(request);

        assertThat(vote).isNotNull();
        assertThat(vote.getId()).isNull();
        assertThat(vote.getMember()).isNull();
        assertThat(vote.getVotingSession()).isNull();
        assertThat(vote.getChoice()).isEqualTo(VoteChoice.NO);
        assertThat(vote.getCreatedAt()).isNotNull();
    }

    @Test
    void returnsNullVoteWhenRequestIsNull() {
        assertThat(converter.convertFromDto(null)).isNull();
    }
}
