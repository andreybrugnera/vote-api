package br.com.sicredi.vote.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.sicredi.vote.model.AgendaStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class AgendaResponseDTO {

    @Schema(description = "Agenda id (UUID)", example = "06d45db0-7c31-476e-82c0-f1f74e5a1782")
    private UUID id;

    @Schema(description = "Agenda description", example = "Aprovação do orçamento 2026")
    private String description;

    @Schema(description = "Agenda status", example = "WAITING_SESSION")
    private AgendaStatus status;

    @Schema(description = "Agenda creation timestamp", example = "2026-07-14T10:15:30")
    private LocalDateTime createdAt;
}
