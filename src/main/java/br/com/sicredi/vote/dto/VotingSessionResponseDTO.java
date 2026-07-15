package br.com.sicredi.vote.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VotingSessionResponseDTO {

    @Schema(description = "Voting session id (UUID)", example = "3f1e9d20-1b2c-4a3d-8e4f-5a6b7c8d9e0f")
    private UUID id;

    @Schema(description = "Agenda id (UUID) the voting session belongs to",
            example = "06d45db0-7c31-476e-82c0-f1f74e5a1782")
    private UUID agendaId;

    @Schema(description = "When the voting session opens", example = "2026-07-14T10:15:30")
    private LocalDateTime openedAt;

    @Schema(description = "When the voting session closes", example = "2026-07-14T10:16:30")
    private LocalDateTime closesAt;

    @Schema(description = "When the voting session result was published", example = "2026-07-14T10:16:35")
    private LocalDateTime resultPublishedAt;

    @Schema(description = "Voting session creation timestamp", example = "2026-07-14T10:14:00")
    private LocalDateTime createdAt;
}
