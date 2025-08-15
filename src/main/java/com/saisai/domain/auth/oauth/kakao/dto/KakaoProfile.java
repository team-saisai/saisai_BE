package com.saisai.domain.auth.oauth.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoProfile(
    @JsonProperty("profile_image_url") String profileImageUrl,
    String nickname
) {
}
