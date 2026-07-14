package br.com.sicredi.vote.converter;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Component;

import br.com.sicredi.vote.dto.MemberRequestDTO;
import br.com.sicredi.vote.dto.MemberResponseDTO;
import br.com.sicredi.vote.model.Member;

@Component
public class MemberConverter extends DefaultConverter
        implements Converter<Member, MemberRequestDTO, MemberResponseDTO> {

    @Override
    public MemberResponseDTO convertToDto(Member member) {
        if (member == null) {
            return null;
        }

        return MemberResponseDTO.builder()
                .id(member.getId())
                .name(member.getName())
                .document(member.getDocument())
                .build();
    }

    @Override
    public Member convertFromDto(MemberRequestDTO memberRequestDTO) {
        if (memberRequestDTO == null) {
            return null;
        }

        Member member = getModelMapper().map(memberRequestDTO, Member.class);
        member.setCreatedAt(OffsetDateTime.now());
        return member;
    }
}
