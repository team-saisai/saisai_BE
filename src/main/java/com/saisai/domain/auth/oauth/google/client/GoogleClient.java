package com.saisai.domain.auth.oauth.google.client;

import static com.saisai.domain.common.exception.ExceptionCode.GOOGLE_UNLINK_FAILED;

import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.common.exception.ExceptionCode;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleClient {

    /**
     * 구글 토큰 폐기
     */
    public void revoke(String token) {

        try {
            RestClient.create()
                .post()
                .uri("https://oauth2.googleapis.com/revoke")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body("token=" + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    String errorMessage = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                    log.error("구글 토큰 폐기 실패. HTTP Status: {}, Body: {}", res.getStatusCode(), errorMessage);
                    throw new CustomException(GOOGLE_UNLINK_FAILED);
                })
                .toBodilessEntity();
        } catch (Exception e) {
            log.error("구글 토큰 폐기 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(ExceptionCode.GOOGLE_AUTH_API_COMMUNICATION_FAILED, e);
        }
    }

}
