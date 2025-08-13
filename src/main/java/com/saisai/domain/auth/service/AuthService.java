package com.saisai.domain.auth.service;

import static com.saisai.domain.auth.constant.ProviderType.GOOGLE;
import static com.saisai.domain.auth.constant.ProviderType.KAKAO;

import com.saisai.config.jwt.JwtProvider;
import com.saisai.domain.auth.dto.request.OauthLoginReq;
import com.saisai.domain.auth.dto.response.TokenRes;
import com.saisai.domain.auth.oauth.UserInfo;
import com.saisai.domain.auth.oauth.google.client.GoogleAndroidClient;
import com.saisai.domain.auth.oauth.google.client.GoogleIosClient;
import com.saisai.domain.auth.oauth.kakao.client.KakaoClient;
import com.saisai.domain.user.entity.User;
import com.saisai.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRedisService refreshTokenRedisService;
    private final KakaoClient kakaoClient;
    private final GoogleAndroidClient googleAndroidClient;
    private final GoogleIosClient googleIosClient;

    @Transactional
    public TokenRes kakaoLogion(OauthLoginReq oauthLoginReq) {
        UserInfo userInfo = kakaoClient.getUserInfo(oauthLoginReq.token());

        User user = userRepository.findByProviderId(userInfo.providerId())
            .orElseGet(() -> {
                User newUser = User.of(userInfo, KAKAO);
                return userRepository.save(newUser);
            });

        return issueAndSaveTokens(user);
    }

    @Transactional
    public TokenRes googleLoginAndroid (OauthLoginReq oauthLoginReq) {
        UserInfo userInfo = googleAndroidClient.verifyAndGetUserInfo(oauthLoginReq.token());

        User user = userRepository.findByProviderId(userInfo.providerId())
            .orElseGet(() -> {
                User newUser = User.of(userInfo, GOOGLE);
                return userRepository.save(newUser);
            });

        return issueAndSaveTokens(user);
    }

    @Transactional
    public TokenRes googleLoginIos (OauthLoginReq oauthLoginReq) {
        UserInfo userInfo = googleIosClient.verifyAndGetUserInfo(oauthLoginReq.token());

        User user = userRepository.findByProviderId(userInfo.providerId())
            .orElseGet(() -> {
                User newUser = User.of(userInfo, GOOGLE);
                return userRepository.save(newUser);
            });

        return issueAndSaveTokens(user);
    }

    // 액세스 토큰, 리프레시 토큰 발급하고 리프레시 토큰을 저장하는 메서드
    private TokenRes issueAndSaveTokens(User user) {
        String newAccessToken = jwtProvider.generateAccessToken(user);
        String newRefreshToken = jwtProvider.generateRefreshToken(user);

        refreshTokenRedisService.saveRefreshToken(user.getId(), jwtProvider.substringToken(newRefreshToken));

        return TokenRes.from(newAccessToken, newRefreshToken);
    }
}
