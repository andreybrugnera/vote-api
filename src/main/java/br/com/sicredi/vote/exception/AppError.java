package br.com.sicredi.vote.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AppError {

    EMPTY_MEMBER_NAME("M-001", "Member name cannot be empty.", HttpStatus.BAD_REQUEST),
    INVALID_MEMBER_DOCUMENT("M-002", "Provided document [{}] is invalid.", HttpStatus.BAD_REQUEST),
    NULL_MEMBER("M-003", "Member cannot be null.", HttpStatus.BAD_REQUEST),
    MEMBER_ALREADY_EXISTS("M-004", "Member with document [{}] already exists.", HttpStatus.BAD_REQUEST),

    EMPTY_AGENDA_DESCRIPTION("A-001", "Agenda description cannot be empty.", HttpStatus.BAD_REQUEST),
    NULL_AGENDA("A-002", "Agenda cannot be null.", HttpStatus.BAD_REQUEST),
    AGENDA_NOT_FOUND("A-003", "Agenda with id [{}] was not found.", HttpStatus.NOT_FOUND),
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
