package com.saisai.domain.auth.dto.response;

public record LoginRes(
    String accessToken,
    String refreshToken,
    boolean isNewUser
) {
    public static LoginRes of (TokenRes tokenRes, boolean isNewUser) {
        return new LoginRes(
            tokenRes.accessToken(),
            tokenRes.refreshToken(),
            isNewUser
        );
    }
}
