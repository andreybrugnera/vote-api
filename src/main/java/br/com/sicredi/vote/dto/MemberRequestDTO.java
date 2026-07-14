package br.com.sicredi.vote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequestDTO {

    @Schema(description = "Member name", example = "Paulo Souza")
    private String name;

    @Schema(description = "Member document (CPF)", example = "04153875074")
    private String document;
}
