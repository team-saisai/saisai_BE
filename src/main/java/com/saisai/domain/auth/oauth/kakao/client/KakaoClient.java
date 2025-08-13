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

    // 💡 디버깅을 위한 임시 테스트 메서드
    public UserInfo testWithHardcodedToken(String testToken) {
        System.out.println("디버깅 테스트 시작");
        try {
            KakaoUser kakaoUser = RestClient.create()
                .get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    System.err.println("디버깅 실패: " + res.getStatusCode());
                    throw new RuntimeException("카카오 사용자 정보 요청에 실패하였습니다.");
                })
                .body(KakaoUser.class);

            System.out.println("디버깅 성공! 사용자 이름: " + kakaoUser.kakaoAccount().name());
            return UserInfo.from(kakaoUser);
        } catch (Exception e) {
            System.err.println("디버깅 중 예외 발생: " + e.getMessage());
        }
        return null;
    }
}
