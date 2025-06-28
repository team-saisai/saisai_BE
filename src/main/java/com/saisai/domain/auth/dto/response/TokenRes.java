package com.saisai.domain.auth.dto.response;

public record TokenRes(
    String accessToken,
    String refreshToken
) {

    public static TokenRes from (String accessToken, String refreshToken) {
        return new TokenRes(
            accessToken,
            refreshToken
        );
    }
}
