package br.com.sicredi.vote.converter;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import br.com.sicredi.vote.dto.AgendaRequestDTO;
import br.com.sicredi.vote.dto.AgendaResponseDTO;
import br.com.sicredi.vote.model.Agenda;

@Component
public class AgendaConverter extends DefaultConverter
        implements Converter<Agenda, AgendaRequestDTO, AgendaResponseDTO> {

    @Override
    public AgendaResponseDTO convertToDto(Agenda agenda) {
        if (agenda == null) {
            return null;
        }

        return AgendaResponseDTO.builder()
                .id(agenda.getId())
                .description(agenda.getDescription())
                .createdAt(agenda.getCreatedAt())
                .build();
    }

    @Override
    public Agenda convertFromDto(AgendaRequestDTO agendaRequestDTO) {
        if (agendaRequestDTO == null) {
            return null;
        }

        Agenda agenda = getModelMapper().map(agendaRequestDTO, Agenda.class);
        agenda.setCreatedAt(LocalDateTime.now());
        return agenda;
    }
}
