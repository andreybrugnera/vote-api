package br.com.sicredi.vote.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.sicredi.vote.dto.VoteRequestDTO;
import br.com.sicredi.vote.dto.VoteResponseDTO;
import br.com.sicredi.vote.exception.AppError;
import br.com.sicredi.vote.exception.BusinessException;
import br.com.sicredi.vote.model.VoteChoice;
import br.com.sicredi.vote.service.VoteService;

@WebMvcTest(VoteController.class)
class VoteControllerTest {

    private static final String VOTES_PATH = "/votes";
    private static final String VOTE_REGISTERED_MESSAGE = "Your vote has been successfully registered";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VoteService voteService;

    @Test
    void createsVoteAndReturnsOk() throws Exception {
        UUID votingSessionId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        UUID voteId = UUID.randomUUID();
        VoteRequestDTO request = new VoteRequestDTO(votingSessionId, memberId, VoteChoice.YES);
        VoteResponseDTO response = VoteResponseDTO.builder()
                .id(voteId)
                .message(VOTE_REGISTERED_MESSAGE)
                .votingSessionId(votingSessionId)
                .memberId(memberId)
                .choice(VoteChoice.YES)
                .build();

        when(voteService.createVote(any(VoteRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post(VOTES_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(voteId.toString()))
                .andExpect(jsonPath("$.data.message").value(VOTE_REGISTERED_MESSAGE))
                .andExpect(jsonPath("$.data.votingSessionId").value(votingSessionId.toString()))
                .andExpect(jsonPath("$.data.memberId").value(memberId.toString()))
                .andExpect(jsonPath("$.data.choice").value(VoteChoice.YES.name()))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void returnsBadRequestWhenMemberAlreadyVoted() throws Exception {
        UUID votingSessionId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        VoteRequestDTO request = new VoteRequestDTO(votingSessionId, memberId, VoteChoice.NO);

        when(voteService.createVote(any(VoteRequestDTO.class)))
                .thenThrow(new BusinessException(AppError.MEMBER_ALREADY_VOTED,
                        String.valueOf(memberId), String.valueOf(votingSessionId)));

        mockMvc.perform(post(VOTES_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorCode").value(AppError.MEMBER_ALREADY_VOTED.getCode()))
                .andExpect(jsonPath("$.errors[0].detail")
                        .value("Member [%s] has already voted on voting session [%s]."
                                .formatted(memberId, votingSessionId)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void returnsNotFoundWhenVotingSessionDoesNotExist() throws Exception {
        UUID votingSessionId = UUID.randomUUID();
        VoteRequestDTO request = new VoteRequestDTO(votingSessionId, UUID.randomUUID(), VoteChoice.YES);

        when(voteService.createVote(any(VoteRequestDTO.class)))
                .thenThrow(new BusinessException(AppError.VOTING_SESSION_NOT_FOUND, String.valueOf(votingSessionId)));

        mockMvc.perform(post(VOTES_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorCode").value(AppError.VOTING_SESSION_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.errors[0].detail")
                        .value("Voting session with id [%s] was not found.".formatted(votingSessionId)));
    }
}
