package com.saisai.domain.auth.oauth.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUser(
    Long id,
    @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {

}
