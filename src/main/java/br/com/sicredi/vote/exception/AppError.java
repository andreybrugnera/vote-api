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
    MEMBER_NOT_FOUND("M-005", "Member with id [{}] was not found.", HttpStatus.NOT_FOUND),

    EMPTY_AGENDA_DESCRIPTION("A-001", "Agenda description cannot be empty.", HttpStatus.BAD_REQUEST),
    NULL_AGENDA("A-002", "Agenda cannot be null.", HttpStatus.BAD_REQUEST),
    AGENDA_NOT_FOUND("A-003", "Agenda with id [{}] was not found.", HttpStatus.NOT_FOUND),

    NULL_VOTING_SESSION("S-001", "Voting session cannot be null.", HttpStatus.BAD_REQUEST),
    NULL_AGENDA_ID("S-002", "Agenda id must be provided.", HttpStatus.BAD_REQUEST),
    INVALID_OPEN_DATE("S-003", "Voting session open date [{}] must be after now.", HttpStatus.BAD_REQUEST),
    SESSION_ALREADY_EXISTS("S-004", "A voting session already exists for agenda [{}].", HttpStatus.BAD_REQUEST),
    VOTING_SESSION_NOT_FOUND("S-005", "Voting session with id [{}] was not found.", HttpStatus.NOT_FOUND),
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
