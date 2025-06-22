package com.saisai.domain.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

    // course
    COURSE_API_CALL_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "CR_ER_05", "코스 API 호출에 실패했습니다."),
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
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
