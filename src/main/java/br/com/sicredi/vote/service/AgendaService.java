package br.com.sicredi.vote.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import br.com.sicredi.vote.converter.AgendaConverter;
import br.com.sicredi.vote.dto.AgendaRequestDTO;
import br.com.sicredi.vote.dto.AgendaResponseDTO;
import br.com.sicredi.vote.exception.AppError;
import br.com.sicredi.vote.exception.BusinessException;
import br.com.sicredi.vote.model.Agenda;
import br.com.sicredi.vote.repository.AgendaRepository;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class AgendaService {

    private AgendaRepository repository;
    private AgendaConverter converter;

    public AgendaResponseDTO createAgenda(AgendaRequestDTO request) throws BusinessException {
        Agenda agenda = converter.convertFromDto(request);
        validateAgenda(agenda);

        agenda = repository.save(agenda);

        return converter.convertToDto(agenda);
    }

    public AgendaResponseDTO getAgenda(UUID id) throws BusinessException {
        Agenda agenda = repository.findById(id)
                .orElseThrow(() -> {
                    log.error(AppError.AGENDA_NOT_FOUND.getMessage(), id);
                    return new BusinessException(AppError.AGENDA_NOT_FOUND, String.valueOf(id));
                });

        return converter.convertToDto(agenda);
    }

    public List<AgendaResponseDTO> listAgendas() {
        List<Agenda> agendas = repository.findAllByOrderByCreatedAtDesc();

        if (CollectionUtils.isEmpty(agendas)) {
            return Collections.emptyList();
        }

        return agendas.stream().map(converter::convertToDto).toList();
    }

    private void validateAgenda(Agenda agenda) throws BusinessException {
        if (agenda == null) {
            log.error(AppError.NULL_AGENDA.getMessage());
            throw new BusinessException(AppError.NULL_AGENDA);
        }

        if (StringUtils.isBlank(agenda.getDescription())) {
            log.error(AppError.EMPTY_AGENDA_DESCRIPTION.getMessage());
            throw new BusinessException(AppError.EMPTY_AGENDA_DESCRIPTION);
        }
    }
}
