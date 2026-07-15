package br.com.sicredi.vote.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.sicredi.vote.dto.ResponseDTO;
import br.com.sicredi.vote.dto.ResponseErrorDTO;
import br.com.sicredi.vote.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ApiExceptionHandler {

    private static final String APPLICATION_EXCEPTION = "Exception thrown: ";

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseErrorDTO> handle(BusinessException ex) {
        log.error(APPLICATION_EXCEPTION, ex);
        return new ResponseEntity(new ResponseDTO<>(new ResponseErrorDTO(ex.getErrorCode(), ex.getMessage())), ex.getHttpStatus());
    }
}
