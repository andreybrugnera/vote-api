package br.com.sicredi.vote.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.sicredi.vote.dto.AgendaRequestDTO;
import br.com.sicredi.vote.dto.AgendaResponseDTO;
import br.com.sicredi.vote.model.Agenda;

class AgendaConverterTest {

    private static final String AGENDA_DESCRIPTION = "Aprovação do orçamento 2026";

    private final AgendaConverter converter = new AgendaConverter();

    @Test
    void convertsAgendaToResponseDto() {
        Agenda agenda = Agenda.builder()
                .id(UUID.randomUUID())
                .description(AGENDA_DESCRIPTION)
                .createdAt(LocalDateTime.now())
                .build();

        AgendaResponseDTO response = converter.convertToDto(agenda);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(agenda.getId());
        assertThat(response.getDescription()).isEqualTo(AGENDA_DESCRIPTION);
        assertThat(response.getCreatedAt()).isEqualTo(agenda.getCreatedAt());
    }

    @Test
    void returnsNullResponseWhenAgendaIsNull() {
        assertThat(converter.convertToDto(null)).isNull();
    }

    @Test
    void convertsRequestDtoToAgenda() {
        AgendaRequestDTO request = new AgendaRequestDTO(AGENDA_DESCRIPTION);

        Agenda agenda = converter.convertFromDto(request);

        assertThat(agenda).isNotNull();
        assertThat(agenda.getDescription()).isEqualTo(AGENDA_DESCRIPTION);
        assertThat(agenda.getId()).isNull();
        assertThat(agenda.getCreatedAt()).isNotNull();
    }

    @Test
    void returnsNullAgendaWhenRequestIsNull() {
        assertThat(converter.convertFromDto(null)).isNull();
    }
}
