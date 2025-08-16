package com.saisai.domain.auth.oauth.apple.client;

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
import com.saisai.domain.common.exception.CustomException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppleClient {

    @Value("${oauth2.apple.client-id}")
    private final String clientId;
    private final JwkProvider jwkProvider;
    private final Random random;

    @Autowired
    public AppleClient(@Value("${oauth2.apple.client-id}") String clientId) {
        this.random = new Random();
        this.clientId = clientId;
        this.jwkProvider = new JwkProviderBuilder("https://appleid.apple.com/auth/keys")
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build();
    }

    public UserInfo verifyAndGetUserInfo(String idToken) {
        DecodedJWT verifiedJwt = verifyToken(idToken);
        return extractUserInfo(verifiedJwt);
    }

    // 토큰 검증
    private DecodedJWT verifyToken(String idToken) {
        try {
            // JWt 토큰 디코딩
            DecodedJWT jwt = JWT.decode(idToken);

            // Apple 공개키 확인 및 알고리즘 생성
            String kid = jwt.getKeyId();
            Jwk jwk = jwkProvider.get(kid);
            PublicKey publicKey = jwk.getPublicKey();
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, null);

            // IdToken 검증
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

    // 유저 정보 추출
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
}
