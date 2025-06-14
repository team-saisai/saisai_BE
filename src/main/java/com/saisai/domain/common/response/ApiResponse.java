package com.saisai.domain.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    private ApiResponse(final String code, final String message, final T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 데이터를 포함한 성공 응답
    public static <T> ApiResponse<T> success(final SuccessCode successCode, final T data) {
        return new ApiResponse<>(successCode.getCode() , successCode.getMessage(), data);
    }

    // 데이터 없이 SuccessCode만 포함한 성공 응답
    public static <T> ApiResponse<T> success(final SuccessCode successCode) {
        return new ApiResponse<>(successCode.getCode(), successCode.getMessage(), null);
    }
}
