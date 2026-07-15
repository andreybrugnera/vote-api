package br.com.sicredi.vote.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.sicredi.vote.dto.AgendaRequestDTO;
import br.com.sicredi.vote.dto.AgendaResponseDTO;
import br.com.sicredi.vote.exception.AppError;
import br.com.sicredi.vote.exception.BusinessException;
import br.com.sicredi.vote.model.AgendaStatus;
import br.com.sicredi.vote.service.AgendaService;

@WebMvcTest(AgendaController.class)
class AgendaControllerTest {

    private static final String AGENDAS_PATH = "/agendas";
    private static final String DESCRIPTION = "Aprovação do orçamento 2026";
    private static final String DESCRIPTION_2 = "Reforma do estatuto";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AgendaService agendaService;

    @Test
    void createsAgendaAndReturnsOk() throws Exception {
        AgendaRequestDTO request = new AgendaRequestDTO(DESCRIPTION);
        AgendaResponseDTO response = response(UUID.randomUUID(), DESCRIPTION);

        when(agendaService.createAgenda(any(AgendaRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post(AGENDAS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.data.description").value(DESCRIPTION))
                .andExpect(jsonPath("$.data.status").value(AgendaStatus.WAITING_SESSION.name()))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void returnsBadRequestWhenCreatingAgendaWithEmptyDescription() throws Exception {
        AgendaRequestDTO request = new AgendaRequestDTO(" ");

        when(agendaService.createAgenda(any(AgendaRequestDTO.class)))
                .thenThrow(new BusinessException(AppError.EMPTY_AGENDA_DESCRIPTION));

        mockMvc.perform(post(AGENDAS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorCode").value(AppError.EMPTY_AGENDA_DESCRIPTION.getCode()))
                .andExpect(jsonPath("$.errors[0].detail").value(AppError.EMPTY_AGENDA_DESCRIPTION.getMessage()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getsAgendaByIdAndReturnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        AgendaResponseDTO response = response(id, DESCRIPTION);

        when(agendaService.getAgenda(id)).thenReturn(response);

        mockMvc.perform(get(AGENDAS_PATH + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.description").value(DESCRIPTION));
    }

    @Test
    void returnsNotFoundWhenAgendaDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();

        when(agendaService.getAgenda(eq(id)))
                .thenThrow(new BusinessException(AppError.AGENDA_NOT_FOUND, String.valueOf(id)));

        mockMvc.perform(get(AGENDAS_PATH + "/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorCode").value(AppError.AGENDA_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.errors[0].detail")
                        .value("Agenda with id [%s] was not found.".formatted(id)));
    }

    @Test
    void returnsBadRequestWhenIdIsNotAValidUuid() throws Exception {
        mockMvc.perform(get(AGENDAS_PATH + "/{id}", "not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listsAgendasAndReturnsOk() throws Exception {
        AgendaResponseDTO first = response(UUID.randomUUID(), DESCRIPTION);
        AgendaResponseDTO second = response(UUID.randomUUID(), DESCRIPTION_2);

        when(agendaService.listAgendas()).thenReturn(List.of(first, second));

        mockMvc.perform(get(AGENDAS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$.data[0].description").value(DESCRIPTION))
                .andExpect(jsonPath("$.data[1].description").value(DESCRIPTION_2));
    }

    @Test
    void returnsNotFoundWhenThereAreNoAgendas() throws Exception {
        when(agendaService.listAgendas()).thenReturn(List.of());

        mockMvc.perform(get(AGENDAS_PATH))
                .andExpect(status().isNotFound());
    }

    private AgendaResponseDTO response(UUID id, String description) {
        return AgendaResponseDTO.builder()
                .id(id)
                .description(description)
                .status(AgendaStatus.WAITING_SESSION)
                .build();
    }
}
