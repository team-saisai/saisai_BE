package com.saisai.config.jwt;

import com.saisai.domain.common.exception.ExceptionCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtAuthenticationException extends AuthenticationException {

    private final HttpStatus httpStatus;
    private final String code;

    public JwtAuthenticationException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.httpStatus = exceptionCode.getHttpStatus();
        this.code = exceptionCode.getCode();
    }
}
