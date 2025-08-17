package com.saisai.domain.auth.oauth.apple.client;

import static com.saisai.domain.common.exception.ExceptionCode.APPLE_AUTH_API_COMMUNICATION_FAILED;
import static com.saisai.domain.common.exception.ExceptionCode.APPLE_CLIENT_SECRET_GENERATION_FAILED;
import static com.saisai.domain.common.exception.ExceptionCode.APPLE_TOKEN_EXCHANGE_FAILED;
import static com.saisai.domain.common.exception.ExceptionCode.APPLE_TOKEN_REVOCATION_FAILED;
import static com.saisai.domain.common.exception.ExceptionCode.EXPIRED_JWT_TOKEN;
import static com.saisai.domain.common.exception.ExceptionCode.INVALID_APPLE_PUBLIC_KEY;
import static com.saisai.domain.common.exception.ExceptionCode.INVALID_JWT_SIGNATURE;
import static com.saisai.domain.common.exception.ExceptionCode.JWK_PROCESSING_ERROR;
import static com.saisai.domain.common.exception.ExceptionCode.JWT_VERIFICATION_FAILED;
import static com.saisai.domain.common.exception.ExceptionCode.MALFORMED_JWT_TOKEN;

import com.auth0.jwk.InvalidPublicKeyException;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.saisai.domain.auth.oauth.UserInfo;
import com.saisai.domain.auth.oauth.apple.response.AppleLoginRes;
import com.saisai.domain.auth.oauth.apple.response.AppleTokenRes;
import com.saisai.domain.common.exception.CustomException;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class AppleClient {

    @Value("${oauth2.apple.client-id}")
    private String clientId;
    @Value("${oauth2.apple.key}")
    private String keyId;
    @Value("${oauth2.apple.team-id}")
    private String teamId;
    @Value("${oauth2.apple.private-key}")
    private String privateKey;

    private JwkProvider jwkProvider;
    private final Random random = new Random();

    @PostConstruct
    public void init() {
        this.jwkProvider = new JwkProviderBuilder("https://appleid.apple.com/auth/keys")
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build();
    }

    /**
     * authorizationCode로 애플 서버와 통신하여 유저 정보와 refreshToken을 모두 가져옴.
     */
    public AppleLoginRes exchangeCodeForUserInfoAndToken(String authorizationCode) {
        String clientSecret = generateClientSecret();
        AppleTokenRes tokens = requestTokens(clientSecret, authorizationCode);

        DecodedJWT verifiedJwt = verifyIdToken(tokens.idToken());
        UserInfo userInfo = extractUserInfo(verifiedJwt);

        return new AppleLoginRes(tokens.refreshToken(), userInfo);
    }

    // client_secret JWT 생성
    private String generateClientSecret() {
        try {
            Date issuedAt = new Date();
            Date expirationDate = Date.from(
                LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant()
            );

            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            PrivateKey privateKeyObject = KeyFactory.getInstance("EC").generatePrivate(keySpec);

            Algorithm algorithm = Algorithm.ECDSA256(null, (ECPrivateKey) privateKeyObject);

            Map<String, Object> headers = new HashMap<>();
            headers.put("kid", keyId);

            return JWT.create()
                .withHeader(headers)
                .withIssuer(teamId)
                .withAudience("https://appleid.apple.com")
                .withSubject(clientId)
                .withIssuedAt(issuedAt)
                .withExpiresAt(expirationDate)
                .sign(algorithm);
        } catch (Exception e) {
            throw new CustomException(APPLE_CLIENT_SECRET_GENERATION_FAILED, e);
        }
    }

    // 애플 서버에 authorizationCode로 토큰 교환 요청
    private AppleTokenRes requestTokens(String clientSecret, String authorizationCode) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", authorizationCode);
        params.add("grant_type", "authorization_code");

        try {
            return RestClient.create()
                .post()
                .uri("https://appleid.apple.com/auth/oauth2/v2/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    String errorMessage = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                    log.warn("Apple RefreshToken 요청 실패: statusCode={}, body={}", res.getStatusCode(), errorMessage);
                    throw new CustomException(APPLE_TOKEN_EXCHANGE_FAILED);
                })
                .body(AppleTokenRes.class);
        } catch (Exception e) {
            log.error("Apple RefreshToken 요청 중 예기치 않은 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(APPLE_AUTH_API_COMMUNICATION_FAILED, e);
        }
    }

    // idToken 검증
    private DecodedJWT verifyIdToken(String idToken) {
        try {
            DecodedJWT jwt = JWT.decode(idToken);
            String kid = jwt.getKeyId();
            Jwk jwk = jwkProvider.get(kid);
            PublicKey publicKey = jwk.getPublicKey();
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, null);

            JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("https://appleid.apple.com")
                .withAudience(clientId)
                .build();
            return verifier.verify(idToken);
        } catch (TokenExpiredException e) {
            throw new CustomException(EXPIRED_JWT_TOKEN, e);
        } catch (SignatureVerificationException e) {
            throw new CustomException(INVALID_JWT_SIGNATURE, e);
        } catch (JWTDecodeException e) {
            throw new CustomException(MALFORMED_JWT_TOKEN, e);
        } catch (InvalidClaimException e) {
            throw new CustomException(JWT_VERIFICATION_FAILED, e);
        } catch (InvalidPublicKeyException e) {
            throw new CustomException(INVALID_APPLE_PUBLIC_KEY, e);
        } catch (JwkException e) {
            throw new CustomException(JWK_PROCESSING_ERROR, e);
        }
    }

    // idToken에서 유저 정보 추출
    private UserInfo extractUserInfo(DecodedJWT verifiedJwt) {
        String providerId = verifiedJwt.getSubject();
        String email = verifiedJwt.getClaim("email").asString();

        String name = "유저" + generateRandomNumber();
        if (verifiedJwt.getClaim("name") != null) {
            String tempName = verifiedJwt.getClaim("name").asString();
            if (tempName != null && !tempName.isEmpty()) {
                name = tempName;
            }
        }
        return new UserInfo(providerId, name, email, null);
    }

    // 난수 생성
    private String generateRandomNumber() {
        int randomNumber = 1000 + this.random.nextInt(9000);
        return String.valueOf(randomNumber);
    }

    // 계정 연결 해제
    public void revoke(String refreshToken) {
        String clientSecret = generateClientSecret();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("token", refreshToken);
        params.add("token_type_hint", "refresh_token");

        try {
            RestClient.create()
                .post()
                .uri("https://appleid.apple.com/auth/oauth2/v2/revoke")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    String errorMessage = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                    log.warn("Apple Revoke 요청 실패: statusCode={}, body={}", res.getStatusCode(), errorMessage);
                    throw new CustomException(APPLE_TOKEN_REVOCATION_FAILED);
                })
                .toBodilessEntity();
        } catch (Exception e) {
            log.error("Apple Revoke 요청 중 예기치 않은 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(APPLE_AUTH_API_COMMUNICATION_FAILED, e);
        }
    }
}
