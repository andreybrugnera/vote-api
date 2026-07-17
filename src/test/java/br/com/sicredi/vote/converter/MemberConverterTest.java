package br.com.sicredi.vote.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.sicredi.vote.dto.MemberRequestDTO;
import br.com.sicredi.vote.dto.MemberResponseDTO;
import br.com.sicredi.vote.model.Member;

class MemberConverterTest {

    private static final String MEMBER_NAME = "Paulo Souza";
    private static final String MEMBER_DOCUMENT = "52998224725";

    private final MemberConverter converter = new MemberConverter();

    @Test
    void convertsMemberToResponseDto() {
        Member member = Member.builder()
                .id(UUID.randomUUID())
                .name(MEMBER_NAME)
                .document(MEMBER_DOCUMENT)
                .createdAt(LocalDateTime.now(ZoneId.systemDefault()))
                .build();

        MemberResponseDTO response = converter.convertToDto(member);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(member.getId());
        assertThat(response.getName()).isEqualTo(MEMBER_NAME);
        assertThat(response.getDocument()).isEqualTo(MEMBER_DOCUMENT);
    }

    @Test
    void returnsNullResponseWhenMemberIsNull() {
        assertThat(converter.convertToDto(null)).isNull();
    }

    @Test
    void convertsRequestDtoToMember() {
        MemberRequestDTO request = new MemberRequestDTO(MEMBER_NAME, MEMBER_DOCUMENT);

        Member member = converter.convertFromDto(request);

        assertThat(member).isNotNull();
        assertThat(member.getName()).isEqualTo(MEMBER_NAME);
        assertThat(member.getDocument()).isEqualTo(MEMBER_DOCUMENT);
        assertThat(member.getId()).isNull();
        assertThat(member.getCreatedAt()).isNotNull();
    }

    @Test
    void returnsNullMemberWhenRequestIsNull() {
        assertThat(converter.convertFromDto(null)).isNull();
    }
}
