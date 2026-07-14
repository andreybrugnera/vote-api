package br.com.sicredi.vote.converter;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Component;

import br.com.sicredi.vote.dto.MemberRequestDTO;
import br.com.sicredi.vote.model.Member;
import br.com.sicredi.vote.model.MemberType;

@Component
public class MemberConverter extends DefaultConverter implements Converter<Member, MemberRequestDTO> {

    @Override
    public MemberRequestDTO convertToDto(Member member) {
        return member != null ? getModelMapper().map(member, MemberRequestDTO.class) : null;
    }

    @Override
    public Member convertFromDto(MemberRequestDTO memberRequestDTO) {
        if (memberRequestDTO == null) {
            return null;
        }

        Member member = getModelMapper().map(memberRequestDTO, Member.class);
        member.setType(MemberType.ASSOCIATE);
        member.setCreatedAt(OffsetDateTime.now());
        return member;
    }
}
