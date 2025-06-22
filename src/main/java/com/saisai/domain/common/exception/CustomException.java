package com.saisai.domain.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String code;

    public CustomException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.httpStatus = exceptionCode.getHttpStatus();
        this.code = exceptionCode.getCode();
    }

    public CustomException(ExceptionCode exceptionCode, Throwable cause) {
        super(exceptionCode.getMessage(), cause);
        this.httpStatus = exceptionCode.getHttpStatus();
        this.code = exceptionCode.getCode();
    }
}
