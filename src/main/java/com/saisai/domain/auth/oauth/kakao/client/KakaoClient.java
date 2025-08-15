package com.saisai.domain.auth.oauth.kakao.client;

import com.saisai.domain.auth.oauth.UserInfo;
import com.saisai.domain.auth.oauth.kakao.dto.KakaoUser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class KakaoClient {

    public UserInfo getUserInfo(String accessToken) {
        KakaoUser kakaoUser = RestClient.create()
            .get()
            .uri("https://kapi.kakao.com/v2/user/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                throw new RuntimeException("카카오 사용자 정보 요청에 실패하였습니다. HTTP Status: " + res.getStatusCode());
            })
            .body(KakaoUser.class);

        return UserInfo.from(kakaoUser);
    }
}
