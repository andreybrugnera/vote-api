package br.com.sicredi.vote.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class MemberResponseDTO {

    @Schema(description = "Member id (UUID)", example = "06d45db0-7c31-476e-82c0-f1f74e5a1782")
    private UUID id;

    @Schema(description = "Member name", example = "Paulo Souza")
    private String name;

    @Schema(description = "Member document (CPF)", example = "04153875074")
    private String document;
}
