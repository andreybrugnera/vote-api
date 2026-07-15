package br.com.sicredi.vote.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sicredi.vote.dto.AgendaRequestDTO;
import br.com.sicredi.vote.dto.AgendaResponseDTO;
import br.com.sicredi.vote.dto.ResponseDTO;
import br.com.sicredi.vote.dto.ResponseErrorDTO;
import br.com.sicredi.vote.exception.BusinessException;
import br.com.sicredi.vote.service.AgendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/agendas")
@AllArgsConstructor
@Slf4j
public class AgendaController {

    private AgendaService agendaService;

    @Tag(name = "Agenda")
    @Operation(
            summary = "Registers a new agenda",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Agenda successfully registered."),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request.",
                            content = @Content(schema =
                            @Schema(implementation = ResponseErrorDTO.class)))
            })
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseDTO> createAgenda(@RequestBody AgendaRequestDTO request) throws BusinessException {
        AgendaResponseDTO response = agendaService.createAgenda(request);
        return ResponseEntity.ok(ResponseDTO.builder()
                .data(response)
                .build());
    }

    @Tag(name = "Agenda")
    @Operation(
            summary = "Get agenda by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Agenda found."),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Agenda not found.",
                            content = @Content(schema =
                            @Schema(implementation = ResponseErrorDTO.class)))
            })
    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseDTO> getAgenda(@PathVariable UUID id) throws BusinessException {
        AgendaResponseDTO response = agendaService.getAgenda(id);
        return ResponseEntity.ok(ResponseDTO.builder()
                .data(response)
                .build());
    }

    @Tag(name = "Agenda")
    @Operation(
            summary = "List all registered agendas ordered by creation date",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Found at least one registered agenda."),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No agendas found."),
            })
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseDTO> listAgendas() {
        List<AgendaResponseDTO> agendas = agendaService.listAgendas();

        if (!CollectionUtils.isEmpty(agendas)) {
            return ResponseEntity.ok(ResponseDTO.builder()
                    .data(agendas)
                    .build());
        }

        return ResponseEntity.notFound().build();
    }
}
