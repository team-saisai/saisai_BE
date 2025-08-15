package com.saisai.domain.auth.oauth;

import com.saisai.domain.auth.oauth.kakao.dto.KakaoUser;

public record UserInfo(
    String providerId,
    String name,
    String email,
    String imageUrl
) {

    public static UserInfo from (KakaoUser kakaoUser) {
        return new UserInfo(
            String.valueOf(kakaoUser.id()),
            kakaoUser.kakaoAccount().profile().nickname(),
            kakaoUser.kakaoAccount().email(),
            kakaoUser.kakaoAccount().profile().profileImageUrl()
        );
    }


}
