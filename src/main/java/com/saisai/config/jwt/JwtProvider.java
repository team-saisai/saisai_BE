package com.saisai.config.jwt;

import static com.saisai.domain.common.exception.ExceptionCode.EXPIRED_JWT_TOKEN;
import static com.saisai.domain.common.exception.ExceptionCode.INTERNAL_SERVER_ERROR;
import static com.saisai.domain.common.exception.ExceptionCode.INVALID_JWT_SIGNATURE;
import static com.saisai.domain.common.exception.ExceptionCode.JWT_TOKEN_REQUIRED;
import static com.saisai.domain.common.exception.ExceptionCode.MALFORMED_JWT_TOKEN;
import static com.saisai.domain.common.exception.ExceptionCode.UNSUPPORTED_JWT_TOKEN;

import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.user.entity.User;
import com.saisai.domain.user.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    private static final String BEARER_PREFIX = "Bearer ";
    public static final Duration REFRESH_TOKEN_TIME = Duration.ofDays(30);
    private static final Duration ACCESS_TOKEN_TIME = Duration.ofHours(1);

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public String generateAccessToken(User user) {
        Date date = new Date();

        return BEARER_PREFIX +
            Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("userRole", user.getRole())
                .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_TIME.toMillis()))
                .setIssuedAt(date)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Date date = new Date();

        return BEARER_PREFIX +
            Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(String.valueOf(user.getId()))
                .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_TIME.toMillis()))
                .setIssuedAt(date)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public Long getUserId(String token){
        Claims claims = getClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    // 토큰 기반으로 사용자 인증 정보 가져오는 메서드
    public AuthUserDetails getAuthentication(String token){
        Claims claims = getClaims(token);

        Long userId = Long.parseLong(claims.getSubject());
        String email = claims.get("email", String.class);
        UserRole userRole = UserRole.of(claims.get("userRole", String.class));

        return AuthUserDetails.from(userId, email, userRole);
    }

    // 토큰 앞에 barear 뗴어주는 메서드
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        throw new CustomException(JWT_TOKEN_REQUIRED);
    }

    // 토큰 유효성 검사하는 메서드
    public boolean validToken(String token) {
        try{
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);

            return true;
        } catch (SecurityException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);
            throw new JwtAuthenticationException(INVALID_JWT_SIGNATURE);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
            throw new JwtAuthenticationException(EXPIRED_JWT_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
            throw new JwtAuthenticationException(UNSUPPORTED_JWT_TOKEN);
        } catch (DecodingException | MalformedJwtException e) {
            log.error("Malformed or Decoding error in JWT token, 형식이 올바르지 않는 JWT 토큰 입니다.", e);
            throw new JwtAuthenticationException(MALFORMED_JWT_TOKEN);
        }catch (Exception e) {
            log.error("Internal server error", e);
            throw new JwtAuthenticationException(INTERNAL_SERVER_ERROR);
        }
    }

}
