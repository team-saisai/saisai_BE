package com.saisai.domain.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
