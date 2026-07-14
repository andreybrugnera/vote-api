package br.com.sicredi.vote.exception;

import lombok.Getter;

@Getter
public enum BusinessError {

    EMPTY_MEMBER_NAME("M-001", "Member name cannot be empty."),
    INVALID_MEMBER_DOCUMENT("M-002", "Provided document [{}] is invalid."),
    NULL_MEMBER("M-003", "Member cannot be null."),
    MEMBER_ALREADY_EXISTS("M-004", "Member with document [{}] already exists."),;

    private final String code;
    private final String message;

    BusinessError(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
