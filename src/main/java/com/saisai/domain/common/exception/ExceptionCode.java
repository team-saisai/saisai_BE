package com.saisai.domain.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

    // auth
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "AUTH_ER_01", "유효하지 않은 UserRole입니다."),
    AUTH_FAILED(HttpStatus.BAD_REQUEST, "AUTH_ER_02", "이메일 또는 비밀번호가 일치하지 않습니다."),
    JWT_TOKEN_REQUIRED(HttpStatus.BAD_REQUEST, "AUTH_ER_03", "JWT 토큰이 필요합니다."),
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "AUTH_ER_04", "유효하지 않는 JWT 서명입니다."),
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_ER_05", "만료된 JWT 토큰입니다."),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "AUTH_ER_06", "지원되지 않는 JWT 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_ER_07", "유효하지 않은 Refresh 토큰입니다."),
    ADMIN_REQUIRED(HttpStatus.FORBIDDEN, "AUTH_ER_08", "관리자 권한이 필요한 요청입니다. 접근 권한이 없습니다."),
    USER_NOT_AUTHENTICATED(HttpStatus.UNAUTHORIZED, "AUTH_ER_09", "인증되지 않았습니다."),


    // user
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_ER_01", "사용자를 찾을 수 없습니다."),
    EMAIL_DUPLICATE(HttpStatus.BAD_REQUEST, "USER_ER_02", "이메일이 중복됩니다."),

    // course
    COURSE_API_CALL_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "CR_ER_05", "코스 API 호출에 실패했습니다."),
    COURSE_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "CR_ER_01", "코스명이 공백으로만 이루어져 있거나 비어있습니다."),
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "CR_ER_03", "코스를 찾을 수 없습니다."),

    // badge
    BADGE_NAME_DUPLICATE(HttpStatus.BAD_REQUEST, "BG_ER_01", "뱃지명이 중복됩니다."),

    // gpx
    GPX_EMPTY(HttpStatus.NOT_FOUND, "GPX_ER_01", "GPX 파일이 비어있습니다."),
    GPX_DOWNLOAD_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "GPX_ER_02", "GPX 파일 다운로드에 실패했습니다."),
    GPX_PARSING_FAILED(HttpStatus.BAD_REQUEST, "GPX_ER_03", "GPX XML 파싱에 실패했습니다."),
    GPX_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GPX_04_ER", "GPX 파일 처리 중 예상치 못한 오류가 발생했습니다."),

    // api
    API_CLIENT_ERROR(HttpStatus.BAD_REQUEST, "API_ER_01", "API 요청에 문제가 발생했습니다. (클라이언트 오류)"),
    API_SERVER_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "API_ER_02", "외부 API 서버에 문제가 발생했습니다. (서버 오류)"),
    API_NETWORK_ERROR(HttpStatus.GATEWAY_TIMEOUT, "API_ER_03", "외부 API와 통신 중 네트워크 오류가 발생했습니다."),
    API_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "API_ER_04", "외부 API 호출 중 알 수 없는 오류가 발생했습니다."),

    // json
    INVALID_JSON_RESPONSE(HttpStatus.INTERNAL_SERVER_ERROR, "JSON_ER_01", "서버 응답에서 JSON을 파싱할 수 없습니다."),

    // format
    INVALID_INTEGER_FORMAT(HttpStatus.BAD_REQUEST, "FMT_ER_01", "Integer 형식 변환에 실패했습니다."),
    INVALID_DOUBLE_FORMAT(HttpStatus.BAD_REQUEST, "FMT_ER_02", "Double 형식 변환에 실패했습니다."),

    // etc
    INVALID_PAGE_NUMBER(HttpStatus.BAD_REQUEST, "ETC_ER_01", "요청하신 페이지 번호가 유효 범위를 초과했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ETC_ER_02", "서버가 응답할 수 없습니다."),
    SERVER_IMAGE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "ETC_ER_03", "이미지 서버에 문제가 생겼습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
