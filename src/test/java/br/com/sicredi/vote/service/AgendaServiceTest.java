package br.com.sicredi.vote.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.sicredi.vote.converter.AgendaConverter;
import br.com.sicredi.vote.dto.AgendaRequestDTO;
import br.com.sicredi.vote.dto.AgendaResponseDTO;
import br.com.sicredi.vote.exception.AppError;
import br.com.sicredi.vote.exception.BusinessException;
import br.com.sicredi.vote.model.Agenda;
import br.com.sicredi.vote.repository.AgendaRepository;

@ExtendWith(MockitoExtension.class)
class AgendaServiceTest {

    private static final String AGENDA_DESCRIPTION = "Aprovação do orçamento 2026";
    private static final String AGENDA_DESCRIPTION_2 = "Eleição do novo suplente";

    @Mock
    private AgendaRepository repository;

    @Mock
    private AgendaConverter converter;

    @InjectMocks
    private AgendaService service;

    @Test
    void createsAgendaWhenRequestIsValid() throws BusinessException {
        AgendaRequestDTO request = new AgendaRequestDTO(AGENDA_DESCRIPTION);
        Agenda agenda = agenda(null, AGENDA_DESCRIPTION);
        Agenda savedAgenda = agenda(UUID.randomUUID(), AGENDA_DESCRIPTION);
        AgendaResponseDTO expectedResponse = responseOf(savedAgenda);

        when(converter.convertFromDto(request)).thenReturn(agenda);
        when(repository.save(agenda)).thenReturn(savedAgenda);
        when(converter.convertToDto(savedAgenda)).thenReturn(expectedResponse);

        AgendaResponseDTO response = service.createAgenda(request);

        assertThat(response).isSameAs(expectedResponse);
        verify(repository).save(agenda);
    }

    @Test
    void rejectsAgendaThatConvertsToNull() {
        when(converter.convertFromDto(null)).thenReturn(null);

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createAgenda(null))
                .withMessage(AppError.NULL_AGENDA.getMessage())
                .extracting(BusinessException::getErrorCode)
                .isEqualTo(AppError.NULL_AGENDA.getCode());

        verify(repository, never()).save(any());
    }

    @Test
    void rejectsAgendaWithoutDescription() {
        AgendaRequestDTO request = new AgendaRequestDTO(" ");
        when(converter.convertFromDto(request)).thenReturn(agenda(null, " "));

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createAgenda(request))
                .withMessage(AppError.EMPTY_AGENDA_DESCRIPTION.getMessage())
                .extracting(BusinessException::getErrorCode)
                .isEqualTo(AppError.EMPTY_AGENDA_DESCRIPTION.getCode());

        verify(repository, never()).save(any());
    }

    @Test
    void getsAgendaById() throws BusinessException {
        Agenda agenda = agenda(UUID.randomUUID(), AGENDA_DESCRIPTION);
        AgendaResponseDTO expectedResponse = responseOf(agenda);

        when(repository.findById(agenda.getId())).thenReturn(Optional.of(agenda));
        when(converter.convertToDto(agenda)).thenReturn(expectedResponse);

        AgendaResponseDTO response = service.getAgenda(agenda.getId());

        assertThat(response).isSameAs(expectedResponse);
    }

    @Test
    void rejectsGetWhenAgendaDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.getAgenda(id))
                .withMessage("Agenda with id [%s] was not found.", id)
                .extracting(BusinessException::getErrorCode)
                .isEqualTo(AppError.AGENDA_NOT_FOUND.getCode());
    }

    @Test
    void listsRegisteredAgendas() {
        Agenda first = agenda(UUID.randomUUID(), AGENDA_DESCRIPTION);
        Agenda second = agenda(UUID.randomUUID(), AGENDA_DESCRIPTION_2);
        AgendaResponseDTO firstResponse = responseOf(first);
        AgendaResponseDTO secondResponse = responseOf(second);

        when(repository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(first, second));
        when(converter.convertToDto(first)).thenReturn(firstResponse);
        when(converter.convertToDto(second)).thenReturn(secondResponse);

        assertThat(service.listAgendas()).containsExactly(firstResponse, secondResponse);
    }

    @Test
    void listsEmptyWhenThereAreNoAgendas() {
        when(repository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());
        assertThat(service.listAgendas()).isEmpty();
    }

    private Agenda agenda(UUID id, String description) {
        return Agenda.builder()
                .id(id)
                .description(description)
                .createdAt(LocalDateTime.now(ZoneId.systemDefault()))
                .build();
    }

    private AgendaResponseDTO responseOf(Agenda agenda) {
        return AgendaResponseDTO.builder()
                .id(agenda.getId())
                .description(agenda.getDescription())
                .createdAt(agenda.getCreatedAt())
                .build();
    }
}
