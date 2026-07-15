package br.com.sicredi.vote.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.sicredi.vote.dto.MemberRequestDTO;
import br.com.sicredi.vote.dto.MemberResponseDTO;
import br.com.sicredi.vote.exception.AppError;
import br.com.sicredi.vote.exception.BusinessException;
import br.com.sicredi.vote.service.MemberService;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    private static final String MEMBERS_PATH = "/members";
    private static final String VALID_DOCUMENT = "52998224725";
    private static final String INVALID_DOCUMENT = "1234567890";
    private static final String MEMBER_NAME = "Paulo Souza";
    private static final String MEMBER_NAME_2 = "Maria Silva";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @Test
    void createsMemberAndReturnsOk() throws Exception {
        MemberRequestDTO request = new MemberRequestDTO(MEMBER_NAME, VALID_DOCUMENT);
        MemberResponseDTO response = response(UUID.randomUUID(), MEMBER_NAME, VALID_DOCUMENT);

        when(memberService.createMember(any(MemberRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post(MEMBERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.data.name").value(MEMBER_NAME))
                .andExpect(jsonPath("$.data.document").value(VALID_DOCUMENT))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void returnsBadRequestWhenCreatingMemberWithInvalidDocument() throws Exception {
        MemberRequestDTO request = new MemberRequestDTO(MEMBER_NAME, INVALID_DOCUMENT);

        when(memberService.createMember(any(MemberRequestDTO.class)))
                .thenThrow(new BusinessException(AppError.INVALID_MEMBER_DOCUMENT, INVALID_DOCUMENT));

        mockMvc.perform(post(MEMBERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorCode").value(AppError.INVALID_MEMBER_DOCUMENT.getCode()))
                .andExpect(jsonPath("$.errors[0].detail")
                        .value("Provided document [%s] is invalid.".formatted(INVALID_DOCUMENT)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getsMemberByIdAndReturnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        MemberResponseDTO response = response(id, MEMBER_NAME, VALID_DOCUMENT);

        when(memberService.getMember(id)).thenReturn(response);

        mockMvc.perform(get(MEMBERS_PATH + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.name").value(MEMBER_NAME))
                .andExpect(jsonPath("$.data.document").value(VALID_DOCUMENT));
    }

    @Test
    void returnsNotFoundWhenMemberDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();

        when(memberService.getMember(eq(id)))
                .thenThrow(new BusinessException(AppError.MEMBER_NOT_FOUND, String.valueOf(id)));

        mockMvc.perform(get(MEMBERS_PATH + "/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorCode").value(AppError.MEMBER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.errors[0].detail")
                        .value("Member with id [%s] was not found.".formatted(id)));
    }

    @Test
    void returnsBadRequestWhenIdIsNotAValidUuid() throws Exception {
        mockMvc.perform(get(MEMBERS_PATH + "/{id}", "not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listsMembersAndReturnsOk() throws Exception {
        MemberResponseDTO first = response(UUID.randomUUID(), MEMBER_NAME, VALID_DOCUMENT);
        MemberResponseDTO second = response(UUID.randomUUID(), MEMBER_NAME_2, "11144477735");

        when(memberService.listMembers()).thenReturn(List.of(first, second));

        mockMvc.perform(get(MEMBERS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$.data[0].name").value(MEMBER_NAME))
                .andExpect(jsonPath("$.data[1].name").value(MEMBER_NAME_2));
    }

    @Test
    void returnsNotFoundWhenThereAreNoMembers() throws Exception {
        when(memberService.listMembers()).thenReturn(List.of());

        mockMvc.perform(get(MEMBERS_PATH))
                .andExpect(status().isNotFound());
    }

    private MemberResponseDTO response(UUID id, String name, String document) {
        return MemberResponseDTO.builder()
                .id(id)
                .name(name)
                .document(document)
                .build();
    }
}
