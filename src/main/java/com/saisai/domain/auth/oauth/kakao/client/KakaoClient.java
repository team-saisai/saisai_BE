package com.saisai.domain.auth.oauth.kakao.client;

import static com.saisai.domain.common.exception.ExceptionCode.KAKAO_UNLINK_FAILED;

import com.saisai.domain.auth.oauth.UserInfo;
import com.saisai.domain.auth.oauth.kakao.dto.KakaoUser;
import com.saisai.domain.common.exception.CustomException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class KakaoClient {

    @Value("${oauth2.kakao.rest-key}")
    private String restKey;
    @Value("${oauth2.kakao.admin-key}")
    private String adminKey;

    // 사용자 정보 조회
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

    // 액세스 토큰으로 카카오 연동 해제
    public void unlink(String accessToken) {
        RestClient.create()
            .post()
            .uri("https://kapi.kakao.com/v1/user/unlink")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                String errorMessage = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                log.error("액세스 토큰으로 카카오 연동 해제 실패. HTTP Status: {}, Body: {}", res.getStatusCode(), errorMessage);
                throw new CustomException(KAKAO_UNLINK_FAILED);
            })
            .toBodilessEntity();
    }

    public void unlinkWithAdminKey(String providerId) {
        RestClient.create()
            .post()
            .uri("https://kapi.kakao.com/v1/user/unlink")
            .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + adminKey)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .body("target_id_type=user_id&target_id=" + providerId)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                String errorMessage = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                log.error("어드민 키로 카카오 연동 해제 실패. HTTP Status: {}, Body: {}", res.getStatusCode(), errorMessage);
                throw new CustomException(KAKAO_UNLINK_FAILED);
            })
            .toBodilessEntity();
    }
}
