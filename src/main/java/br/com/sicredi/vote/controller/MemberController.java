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

import br.com.sicredi.vote.dto.MemberRequestDTO;
import br.com.sicredi.vote.dto.MemberResponseDTO;
import br.com.sicredi.vote.dto.ResponseDTO;
import br.com.sicredi.vote.dto.ResponseErrorDTO;
import br.com.sicredi.vote.exception.BusinessException;
import br.com.sicredi.vote.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/members")
@AllArgsConstructor
@Slf4j
public class MemberController {

    private MemberService memberService;

    @Tag(name = "Members")
    @Operation(
            summary = "Registers a new member",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Member successfully registered."),
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
    public ResponseEntity<ResponseDTO> createMember(@RequestBody MemberRequestDTO request) throws BusinessException {
        MemberResponseDTO response = memberService.createMember(request);
        return ResponseEntity.ok(ResponseDTO.builder()
                .data(response)
                .build());
    }

    @Tag(name = "Members")
    @Operation(
            summary = "Get member by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Member found."),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Member not found.",
                            content = @Content(schema =
                            @Schema(implementation = ResponseErrorDTO.class)))
            })
    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseDTO> getAgenda(@PathVariable UUID id) throws BusinessException {
        MemberResponseDTO response = memberService.getMember(id);
        return ResponseEntity.ok(ResponseDTO.builder()
                .data(response)
                .build());
    }

    @Tag(name = "Members")
    @Operation(
            summary = "List all registered members",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Found at least one registered member."),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No members found."),
            })
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseDTO> listMembers() {
        List<MemberResponseDTO> members = memberService.listMembers();

        if (!CollectionUtils.isEmpty(members)) {
            return ResponseEntity.ok(ResponseDTO.builder()
                    .data(members)
                    .build());
        }

        return ResponseEntity.notFound().build();
    }
}
