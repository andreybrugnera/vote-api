package br.com.sicredi.vote.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.sicredi.vote.converter.MemberConverter;
import br.com.sicredi.vote.dto.MemberRequestDTO;
import br.com.sicredi.vote.dto.MemberResponseDTO;
import br.com.sicredi.vote.exception.AppError;
import br.com.sicredi.vote.exception.BusinessException;
import br.com.sicredi.vote.model.Member;
import br.com.sicredi.vote.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    private static final String VALID_DOCUMENT = "52998224725";
    private static final String VALID_DOCUMENT_2 = "11144477735";
    private static final String INVALID_DOCUMENT = "1234567890";
    private static final String MEMBER_NAME = "Paulo Souza";
    private static final String MEMBER_NAME_2 = "Maria Silva";

    @Mock
    private MemberRepository repository;

    @Mock
    private MemberConverter converter;

    @InjectMocks
    private MemberService service;

    @Test
    void createsMemberWhenRequestIsValid() throws BusinessException {
        MemberRequestDTO request = new MemberRequestDTO(MEMBER_NAME, VALID_DOCUMENT);
        Member member = member(null, MEMBER_NAME, VALID_DOCUMENT);
        Member savedMember = member(UUID.randomUUID(), MEMBER_NAME, VALID_DOCUMENT);
        MemberResponseDTO expectedResponse = responseOf(savedMember);

        when(converter.convertFromDto(request)).thenReturn(member);
        when(repository.findByDocument(VALID_DOCUMENT)).thenReturn(Optional.empty());
        when(repository.save(member)).thenReturn(savedMember);
        when(converter.convertToDto(savedMember)).thenReturn(expectedResponse);

        MemberResponseDTO response = service.createMember(request);

        assertThat(response).isSameAs(expectedResponse);
        verify(repository).save(member);
    }

    @Test
    void rejectsMemberThatConvertsToNull() {
        when(converter.convertFromDto(null)).thenReturn(null);

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createMember(null))
                .withMessage(AppError.NULL_MEMBER.getMessage())
                .extracting(BusinessException::getErrorCode)
                .isEqualTo(AppError.NULL_MEMBER.getCode());

        verify(repository, never()).save(any());
    }

    @Test
    void rejectsMemberWithoutName() {
        MemberRequestDTO request = new MemberRequestDTO(" ", VALID_DOCUMENT);
        when(converter.convertFromDto(request)).thenReturn(member(null, " ", VALID_DOCUMENT));

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createMember(request))
                .withMessage(AppError.EMPTY_MEMBER_NAME.getMessage())
                .extracting(BusinessException::getErrorCode)
                .isEqualTo(AppError.EMPTY_MEMBER_NAME.getCode());

        verify(repository, never()).save(any());
    }

    @Test
    void rejectsMemberWithInvalidDocument() {
        MemberRequestDTO request = new MemberRequestDTO(MEMBER_NAME, INVALID_DOCUMENT);
        when(converter.convertFromDto(request)).thenReturn(member(null, MEMBER_NAME, INVALID_DOCUMENT));

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createMember(request))
                .withMessage("Provided document [%s] is invalid.", INVALID_DOCUMENT)
                .extracting(BusinessException::getErrorCode)
                .isEqualTo(AppError.INVALID_MEMBER_DOCUMENT.getCode());

        verify(repository, never()).save(any());
    }

    @Test
    void rejectsMemberWithDocumentAlreadyRegistered() {
        MemberRequestDTO request = new MemberRequestDTO(MEMBER_NAME, VALID_DOCUMENT);
        Member member = member(null, MEMBER_NAME, VALID_DOCUMENT);
        Member existingMember = member(UUID.randomUUID(), MEMBER_NAME_2, VALID_DOCUMENT);

        when(converter.convertFromDto(request)).thenReturn(member);
        when(repository.findByDocument(VALID_DOCUMENT)).thenReturn(Optional.of(existingMember));

        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.createMember(request))
                .withMessage("Member with document [%s] already exists.", VALID_DOCUMENT)
                .extracting(BusinessException::getErrorCode)
                .isEqualTo(AppError.MEMBER_ALREADY_EXISTS.getCode());

        verify(repository, never()).save(any());
    }

    @Test
    void listsRegisteredMembers() {
        Member first = member(UUID.randomUUID(), MEMBER_NAME, VALID_DOCUMENT);
        Member second = member(UUID.randomUUID(), "Maria Silva", VALID_DOCUMENT_2);
        MemberResponseDTO firstResponse = responseOf(first);
        MemberResponseDTO secondResponse = responseOf(second);

        when(repository.findAll()).thenReturn(List.of(first, second));
        when(converter.convertToDto(first)).thenReturn(firstResponse);
        when(converter.convertToDto(second)).thenReturn(secondResponse);

        assertThat(service.listMembers()).containsExactly(firstResponse, secondResponse);
    }

    @Test
    void listsEmptyWhenThereAreNoMembers() {
        when(repository.findAll()).thenReturn(List.of());
        assertThat(service.listMembers()).isEmpty();
    }

    private Member member(UUID id, String name, String document) {
        return Member.builder()
                .id(id)
                .name(name)
                .document(document)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private MemberResponseDTO responseOf(Member member) {
        return MemberResponseDTO.builder()
                .id(member.getId())
                .name(member.getName())
                .document(member.getDocument())
                .build();
    }
}