package br.com.sicredi.vote.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sicredi.vote.dto.ResponseDTO;
import br.com.sicredi.vote.dto.ResponseErrorDTO;
import br.com.sicredi.vote.dto.VotingSessionRequestDTO;
import br.com.sicredi.vote.dto.VotingSessionResponseDTO;
import br.com.sicredi.vote.exception.BusinessException;
import br.com.sicredi.vote.service.VotingSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/sessions")
@AllArgsConstructor
@Slf4j
public class VotingSessionController {

    private VotingSessionService votingSessionService;

    @Tag(name = "Voting Session")
    @Operation(
            summary = "Creates a new voting session",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Voting session successfully created."),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request.",
                            content = @Content(schema =
                            @Schema(implementation = ResponseErrorDTO.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Agenda not found.",
                            content = @Content(schema =
                            @Schema(implementation = ResponseErrorDTO.class)))
            })
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseDTO> createVotingSession(@RequestBody VotingSessionRequestDTO request)
            throws BusinessException {
        VotingSessionResponseDTO response = votingSessionService.createVotingSession(request);
        return ResponseEntity.ok(ResponseDTO.builder()
                .data(response)
                .build());
    }

    @Tag(name = "Voting Session")
    @Operation(
            summary = "Get voting session by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Voting session found."),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Voting session not found.",
                            content = @Content(schema =
                            @Schema(implementation = ResponseErrorDTO.class)))
            })
    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseDTO> getVotingSession(@PathVariable UUID id) throws BusinessException {
        VotingSessionResponseDTO response = votingSessionService.getVotingSession(id);
        return ResponseEntity.ok(ResponseDTO.builder()
                .data(response)
                .build());
    }

    @Tag(name = "Voting Session")
    @Operation(
            summary = "List all voting sessions ordered by creation date",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Found at least one voting session."),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No voting sessions found."),
            })
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseDTO> listVotingSessions() {
        List<VotingSessionResponseDTO> votingSessions = votingSessionService.listVotingSessions();

        if (!CollectionUtils.isEmpty(votingSessions)) {
            return ResponseEntity.ok(ResponseDTO.builder()
                    .data(votingSessions)
                    .build());
        }

        return ResponseEntity.notFound().build();
    }
}
