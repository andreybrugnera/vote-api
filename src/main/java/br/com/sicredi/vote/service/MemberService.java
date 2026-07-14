package br.com.sicredi.vote.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import br.com.sicredi.vote.converter.MemberConverter;
import br.com.sicredi.vote.dto.MemberRequestDTO;
import br.com.sicredi.vote.dto.MemberResponseDTO;
import br.com.sicredi.vote.exception.BusinessError;
import br.com.sicredi.vote.exception.BusinessException;
import br.com.sicredi.vote.model.Member;
import br.com.sicredi.vote.repository.MemberRepository;
import br.com.sicredi.vote.validator.DocumentValidator;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class MemberService {

    private MemberRepository repository;
    private MemberConverter converter;

    public MemberResponseDTO createMember(MemberRequestDTO request) throws BusinessException {
        Member member = converter.convertFromDto(request);
        validateMember(member);

        member = repository.save(member);

        return converter.convertToDto(member);
    }

    public List<MemberResponseDTO> listMembers() {
        List<Member> members = repository.findAll();

        if (CollectionUtils.isEmpty(members)) {
            return Collections.emptyList();
        }

        return members.stream().map(converter::convertToDto).toList();
    }

    private void validateMember(Member member) throws BusinessException {
        if (member == null) {
            log.error(BusinessError.NULL_MEMBER.getMessage());
            throw new BusinessException(BusinessError.NULL_MEMBER);
        }

        if (StringUtils.isBlank(member.getName())) {
            log.error(BusinessError.EMPTY_MEMBER_NAME.getMessage());
            throw new BusinessException(BusinessError.EMPTY_MEMBER_NAME);
        }

        if (!DocumentValidator.validateDocument(member.getDocument())) {
            log.error(BusinessError.INVALID_MEMBER_DOCUMENT.getMessage(), member.getDocument());
            throw new BusinessException(BusinessError.INVALID_MEMBER_DOCUMENT, member.getDocument());
        }

        if (repository.findByDocument(member.getDocument()).isPresent()) {
            log.error(BusinessError.MEMBER_ALREADY_EXISTS.getMessage(), member.getDocument());
            throw new BusinessException(BusinessError.MEMBER_ALREADY_EXISTS, member.getDocument());
        }
    }
}
