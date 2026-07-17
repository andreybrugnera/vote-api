package br.com.sicredi.vote.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.sicredi.vote.dto.VotingSessionRequestDTO;
import br.com.sicredi.vote.dto.VotingSessionResponseDTO;
import br.com.sicredi.vote.exception.AppError;
import br.com.sicredi.vote.exception.BusinessException;
import br.com.sicredi.vote.service.VotingSessionService;

@WebMvcTest(VotingSessionController.class)
class VotingSessionControllerTest {

    private static final String SESSIONS_PATH = "/sessions";

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VotingSessionService votingSessionService;

    @Test
    void createsVotingSessionAndReturnsOk() throws Exception {
        UUID agendaId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        LocalDateTime openedAt = LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(5);
        VotingSessionRequestDTO request = new VotingSessionRequestDTO(agendaId, openedAt, null);
        VotingSessionResponseDTO response = response(sessionId, agendaId, openedAt);

        when(votingSessionService.createVotingSession(any(VotingSessionRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post(SESSIONS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(sessionId.toString()))
                .andExpect(jsonPath("$.data.agendaId").value(agendaId.toString()))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void returnsBadRequestWhenSessionAlreadyExists() throws Exception {
        UUID agendaId = UUID.randomUUID();
        VotingSessionRequestDTO request =
                new VotingSessionRequestDTO(agendaId, LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(5), null);

        when(votingSessionService.createVotingSession(any(VotingSessionRequestDTO.class)))
                .thenThrow(new BusinessException(AppError.SESSION_ALREADY_EXISTS, String.valueOf(agendaId)));

        mockMvc.perform(post(SESSIONS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorCode").value(AppError.SESSION_ALREADY_EXISTS.getCode()))
                .andExpect(jsonPath("$.errors[0].detail")
                        .value("A voting session already exists for agenda [%s].".formatted(agendaId)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void returnsNotFoundWhenAgendaDoesNotExist() throws Exception {
        UUID agendaId = UUID.randomUUID();
        VotingSessionRequestDTO request =
                new VotingSessionRequestDTO(agendaId, LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(5), null);

        when(votingSessionService.createVotingSession(any(VotingSessionRequestDTO.class)))
                .thenThrow(new BusinessException(AppError.AGENDA_NOT_FOUND, String.valueOf(agendaId)));

        mockMvc.perform(post(SESSIONS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorCode").value(AppError.AGENDA_NOT_FOUND.getCode()));
    }

    @Test
    void getsVotingSessionByIdAndReturnsOk() throws Exception {
        UUID sessionId = UUID.randomUUID();
        UUID agendaId = UUID.randomUUID();
        VotingSessionResponseDTO response = response(sessionId, agendaId, LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(5));

        when(votingSessionService.getVotingSession(sessionId)).thenReturn(response);

        mockMvc.perform(get(SESSIONS_PATH + "/{id}", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(sessionId.toString()))
                .andExpect(jsonPath("$.data.agendaId").value(agendaId.toString()));
    }

    @Test
    void returnsNotFoundWhenVotingSessionDoesNotExist() throws Exception {
        UUID sessionId = UUID.randomUUID();

        when(votingSessionService.getVotingSession(eq(sessionId)))
                .thenThrow(new BusinessException(AppError.VOTING_SESSION_NOT_FOUND, String.valueOf(sessionId)));

        mockMvc.perform(get(SESSIONS_PATH + "/{id}", sessionId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorCode").value(AppError.VOTING_SESSION_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.errors[0].detail")
                        .value("Voting session with id [%s] was not found.".formatted(sessionId)));
    }

    @Test
    void returnsBadRequestWhenIdIsNotAValidUuid() throws Exception {
        mockMvc.perform(get(SESSIONS_PATH + "/{id}", "not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listsVotingSessionsAndReturnsOk() throws Exception {
        VotingSessionResponseDTO first =
                response(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(5));
        VotingSessionResponseDTO second =
                response(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(10));

        when(votingSessionService.listVotingSessions()).thenReturn(List.of(first, second));

        mockMvc.perform(get(SESSIONS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(first.getId().toString()))
                .andExpect(jsonPath("$.data[1].id").value(second.getId().toString()));
    }

    @Test
    void returnsNotFoundWhenThereAreNoVotingSessions() throws Exception {
        when(votingSessionService.listVotingSessions()).thenReturn(List.of());

        mockMvc.perform(get(SESSIONS_PATH))
                .andExpect(status().isNotFound());
    }

    private VotingSessionResponseDTO response(UUID id, UUID agendaId, LocalDateTime openedAt) {
        return VotingSessionResponseDTO.builder()
                .id(id)
                .agendaId(agendaId)
                .openedAt(openedAt)
                .closesAt(openedAt.plusMinutes(1))
                .build();
    }
}
