package com.saisai.domain.auth.oauth.google.client;

import static com.saisai.domain.common.exception.ExceptionCode.GOOGLE_ID_TOKEN_VERIFICATION_FAILED;
import static com.saisai.domain.common.exception.ExceptionCode.INVALID_GOOGLE_ID_TOKEN;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.saisai.domain.auth.oauth.UserInfo;
import com.saisai.domain.common.exception.CustomException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GoogleIosClient {

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new GsonFactory();

    private final GoogleIdTokenVerifier verifier;

    public GoogleIosClient(@Value("${oauth2.google.ios.client-id}") String clientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(HTTP_TRANSPORT, JSON_FACTORY)
            .setAudience(Collections.singletonList(clientId))
            .build();
    }

    /**
     * Google ID 토큰을 검증하고 유효하면 사용자 정보를 반환
     *
     * @param idTokenString Google ID 토큰 문자열
     * @return 유효한 경우 UserInfo DTO, 유효하지 않은 경우 CustomException 발생
     */
    public UserInfo verifyAndGetUserInfo(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new CustomException(INVALID_GOOGLE_ID_TOKEN);
            }

            Payload payload = idToken.getPayload();

            String providerId = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture");

            return new UserInfo(providerId, name, email, picture);

        } catch (GeneralSecurityException | IOException e) {
            throw new CustomException(GOOGLE_ID_TOKEN_VERIFICATION_FAILED, e);
        }
    }

}
