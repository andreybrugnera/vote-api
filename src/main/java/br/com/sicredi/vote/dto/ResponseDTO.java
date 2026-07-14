package br.com.sicredi.vote.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO<E> {

    private E data;
    private List<ResponseErrorDTO> errors;

    public ResponseDTO(ResponseErrorDTO error) {
        this.errors = List.of(error);
    }
}
