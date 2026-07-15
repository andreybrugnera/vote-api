package br.com.sicredi.vote.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.sicredi.vote.model.VoteChoice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoteResponseDTO {

    @Schema(description = "Vote registration confirmation message",
            example = "Your vote has been successfully registered")
    private String message;

    @Schema(description = "Vote id (UUID)", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d")
    private UUID id;

    @Schema(description = "Voting session id (UUID)",
            example = "3f1e9d20-1b2c-4a3d-8e4f-5a6b7c8d9e0f")
    private UUID votingSessionId;

    @Schema(description = "Member id (UUID)",
            example = "9c1b2a30-4d5e-6f7a-8b9c-0d1e2f3a4b5c")
    private UUID memberId;

    @Schema(description = "Vote choice", example = "YES")
    private VoteChoice choice;

    @Schema(description = "Vote creation timestamp", example = "2026-07-14T10:15:45")
    private LocalDateTime createdAt;
}
