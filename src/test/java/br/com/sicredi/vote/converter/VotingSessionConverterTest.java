package br.com.sicredi.vote.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.sicredi.vote.dto.VotingSessionRequestDTO;
import br.com.sicredi.vote.dto.VotingSessionResponseDTO;
import br.com.sicredi.vote.model.Agenda;
import br.com.sicredi.vote.model.VotingSession;

class VotingSessionConverterTest {

    private final VotingSessionConverter converter = new VotingSessionConverter();

    @Test
    void convertsVotingSessionToResponseDto() {
        UUID agendaId = UUID.randomUUID();
        LocalDateTime openedAt = LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(5);
        VotingSession votingSession = VotingSession.builder()
                .id(UUID.randomUUID())
                .agenda(Agenda.builder().id(agendaId).build())
                .openedAt(openedAt)
                .closesAt(openedAt.plusMinutes(1))
                .resultPublishedAt(openedAt.plusMinutes(2))
                .createdAt(LocalDateTime.now(ZoneId.systemDefault()))
                .build();

        VotingSessionResponseDTO response = converter.convertToDto(votingSession);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(votingSession.getId());
        assertThat(response.getAgendaId()).isEqualTo(agendaId);
        assertThat(response.getOpenedAt()).isEqualTo(votingSession.getOpenedAt());
        assertThat(response.getClosesAt()).isEqualTo(votingSession.getClosesAt());
        assertThat(response.getResultPublishedAt()).isEqualTo(votingSession.getResultPublishedAt());
        assertThat(response.getCreatedAt()).isEqualTo(votingSession.getCreatedAt());
    }

    @Test
    void returnsNullResponseWhenVotingSessionIsNull() {
        assertThat(converter.convertToDto(null)).isNull();
    }

    @Test
    void convertsRequestDtoToVotingSession() {
        LocalDateTime openedAt = LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(5);
        VotingSessionRequestDTO request = new VotingSessionRequestDTO(UUID.randomUUID(), openedAt, null);

        VotingSession votingSession = converter.convertFromDto(request);

        assertThat(votingSession).isNotNull();
        assertThat(votingSession.getId()).isNull();
        assertThat(votingSession.getOpenedAt()).isEqualTo(openedAt);
        assertThat(votingSession.getCreatedAt()).isNotNull();
    }

    @Test
    void returnsNullVotingSessionWhenRequestIsNull() {
        assertThat(converter.convertFromDto(null)).isNull();
    }
}
