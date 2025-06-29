package com.saisai.domain.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    // Auth
    REGISTER_SUCCESS(HttpStatus.CREATED, "AUTH_01", "회원가입에 성공했습니다."),
    LOGIN_SUCCESS(HttpStatus.OK, "AUTH_02", "로그인에 성공했습니다."),
    REISSUE_SUCCESS(HttpStatus.OK, "AUTH_03", "토큰 재발급에 성공했습니다."),

    // Course
    COURSE_LIST_GET_SUCCESS(HttpStatus.OK, "COURSE_01", "코스 전체 목록 조회에 성공했습니다."),
    COURSE_INFO_GET_SUCCESS(HttpStatus.OK, "COURSE_02", "코스 단일 상세 조회에 성공했습니다."),

    // Challenge
    CHALLENGE_POPULAR_LIST_GET_SUCCESS(HttpStatus.OK, "CHALLENGE_04", "인기 챌린지 리스트 조회에 성공했습니다."),

    // Ride
    RECENT_RIDE_COURSE_GET_SUCCESS(HttpStatus.OK, "RIDE_01", "최근 주행한 코스 조회에 성공했습니다."),

    // Badge
    BADGE_CREATE_SUCCESS(HttpStatus.CREATED, "BADGE_01", "뱃지 생성에 성공했습니다."),
    MY_BADGE_LIST_GET_SUCCESS(HttpStatus.OK, "BADGE_02", "로그인 한 사용자의 뱃지 리스트 조회에 성공했습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
