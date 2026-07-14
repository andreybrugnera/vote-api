package br.com.sicredi.vote.exception;

import org.slf4j.helpers.MessageFormatter;

import lombok.Getter;

@Getter
public class BusinessException extends Exception {

    private final String errorCode;

    public BusinessException(BusinessError error, String... params) {
        super(MessageFormatter.arrayFormat(error.getMessage(), params).getMessage());
        this.errorCode = error.getCode();
    }
}
