package br.com.sicredi.vote.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sicredi.vote.dto.ResponseDTO;
import br.com.sicredi.vote.dto.ResponseErrorDTO;
import br.com.sicredi.vote.dto.VoteRequestDTO;
import br.com.sicredi.vote.dto.VoteResponseDTO;
import br.com.sicredi.vote.exception.BusinessException;
import br.com.sicredi.vote.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/votes")
@AllArgsConstructor
@Slf4j
public class VoteController {

    private VoteService voteService;

    @Tag(name = "Vote")
    @Operation(
            summary = "Registers a new vote on a voting session",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Vote successfully registered."),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request (e.g. voting session closed or member already voted).",
                            content = @Content(schema =
                            @Schema(implementation = ResponseErrorDTO.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Member or voting session not found.",
                            content = @Content(schema =
                            @Schema(implementation = ResponseErrorDTO.class)))
            })
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseDTO> createVote(@RequestBody VoteRequestDTO request)
            throws BusinessException {
        VoteResponseDTO response = voteService.createVote(request);
        return ResponseEntity.ok(ResponseDTO.builder()
                .data(response)
                .build());
    }
}
