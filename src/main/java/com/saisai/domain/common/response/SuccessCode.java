package com.saisai.domain.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    // Course
    COURSE_LIST_GET_SUCCESS(HttpStatus.OK, "COURSE_01", "코스 전체 목록 조회에 성공했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
