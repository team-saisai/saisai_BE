package com.saisai.domain.auth.oauth.kakao.dto;

public record KakaoAccount(
    String email,
    String name,
    KakaoProfile profile
) {

}
