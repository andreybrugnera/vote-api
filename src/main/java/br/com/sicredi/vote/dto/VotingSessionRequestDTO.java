package br.com.sicredi.vote.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VotingSessionRequestDTO {

    @Schema(description = "Agenda id (UUID) the voting session will be opened on",
            example = "06d45db0-7c31-476e-82c0-f1f74e5a1782")
    private UUID agendaId;

    @Schema(description = "When the voting session opens (must be after now)",
            example = "2026-07-14T10:15:30")
    private LocalDateTime openedAt;

    @Schema(description = "When the voting session closes (optional; defaults to one minute after openedAt)",
            example = "2026-07-14T10:16:30")
    private LocalDateTime closesAt;
}
