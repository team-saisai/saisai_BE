package com.saisai.domain.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    // Auth
    REGISTER_SUCCESS(HttpStatus.CREATED, "AUTH_01", "회원가입에 성공했습니다."),
    // Course
    COURSE_LIST_GET_SUCCESS(HttpStatus.OK, "COURSE_01", "코스 전체 목록 조회에 성공했습니다."),
    COURSE_INFO_GET_SUCCESS(HttpStatus.OK, "COURSE_02", "코스 단일 상세 조회에 성공했습니다."),

    // Challenge
    CHALLENGE_POPULAR_LIST_GET_SUCCESS(HttpStatus.OK, "CHALLENGE_04", "인기 챌린지 리스트 조회에 성공했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
