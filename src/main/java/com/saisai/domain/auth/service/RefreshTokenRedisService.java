package com.saisai.domain.auth.service;

import static com.saisai.config.jwt.JwtProvider.REFRESH_TOKEN_TIME;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenRedisService {

    private final StringRedisTemplate stringRedisTemplate;
    private static final String REFRESH_TOKEN_REDIS_KEY = "refreshToken:";

    public String getRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_REDIS_KEY + userId.toString();
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public void saveRefreshToken(Long userId, String refreshToken){
        String key = REFRESH_TOKEN_REDIS_KEY + userId.toString();
        ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set(key, refreshToken, REFRESH_TOKEN_TIME);
    }

    public void deleteRefreshToken(Long userId){
        String key = REFRESH_TOKEN_REDIS_KEY + userId.toString();
        stringRedisTemplate.delete(key);
    }
}
