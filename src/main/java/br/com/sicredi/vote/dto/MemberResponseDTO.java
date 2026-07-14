package br.com.sicredi.vote.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class MemberResponseDTO {

    private UUID id;
    private String name;
    private String document;
}
