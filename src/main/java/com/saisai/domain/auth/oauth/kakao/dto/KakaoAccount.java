package com.saisai.domain.auth.oauth.kakao.dto;

public record KakaoAccount(
    String email,
    KakaoProfile profile
) {

}
