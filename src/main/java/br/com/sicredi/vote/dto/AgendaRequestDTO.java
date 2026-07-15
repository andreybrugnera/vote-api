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
public class AgendaRequestDTO {

    @Schema(description = "Agenda description", example = "Aprovação do orçamento 2026")
    private String description;
}
