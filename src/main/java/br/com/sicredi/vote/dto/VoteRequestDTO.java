package br.com.sicredi.vote.dto;

import java.util.UUID;

import br.com.sicredi.vote.model.VoteChoice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VoteRequestDTO {

    @Schema(description = "Voting session id (UUID)",
            example = "3f1e9d20-1b2c-4a3d-8e4f-5a6b7c8d9e0f")
    private UUID votingSessionId;

    @Schema(description = "Member id (UUID)",
            example = "9c1b2a30-4d5e-6f7a-8b9c-0d1e2f3a4b5c")
    private UUID memberId;

    @Schema(description = "Vote choice", example = "YES")
    private VoteChoice choice;
}
