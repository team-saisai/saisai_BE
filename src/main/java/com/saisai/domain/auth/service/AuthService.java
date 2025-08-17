package com.saisai.domain.auth.service;

import static com.saisai.domain.auth.constant.ProviderType.APPLE;
import static com.saisai.domain.auth.constant.ProviderType.GOOGLE;
import static com.saisai.domain.auth.constant.ProviderType.KAKAO;

import com.saisai.config.jwt.JwtProvider;
import com.saisai.domain.auth.dto.request.OauthLoginReq;
import com.saisai.domain.auth.dto.response.TokenRes;
import com.saisai.domain.auth.oauth.UserInfo;
import com.saisai.domain.auth.oauth.apple.client.AppleClient;
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
    private final AppleClient appleClient;

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

    @Transactional
    public TokenRes appleLogin(OauthLoginReq oauthLoginReq) {

        UserInfo userInfo = appleClient.verifyAndGetUserInfo(oauthLoginReq.token());

        User user = userRepository.findByProviderId(userInfo.providerId())
            .orElseGet(() -> {
                User newUser = User.of(userInfo, APPLE);
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

    @Transactional
    public WithdrawRes deleteUser(AuthUserDetails authUserDetails, String accessToken, WithdrawReq withdrawReq) {
        refreshTokenRedisService.deleteRefreshToken(authUserDetails.userId());

        jwtProvider.addTokenToBlacklist(accessToken);

        User user = userRepository.findById(authUserDetails.userId())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        switch (user.getProvider()) {
            case APPLE:
                String appleRefreshToken = refreshTokenService.getRefreshToken(
                    user.getProviderId());
                String decryptedToken = tokenEncryptor.decrypt(appleRefreshToken);
                appleClient.revoke(decryptedToken);
                refreshTokenService.deleteRefreshToken(user.getProviderId());
                break;
            case KAKAO:
                try {
                    kakaoClient.unlink(withdrawReq.socialAccessToken());
                    log.info("카카오 연동 해제 성공. providerId: {}", user.getProviderId());
                } catch (CustomException e) {
                    log.warn("카카오 연동 해제 기본 로직 실패. 어드민 키로 재시도. providerId: {}", user.getProviderId(),
                        e);
                    kakaoClient.unlinkWithAdminKey(user.getProviderId());
                    log.info("어드민 키로 카카오 연동 해제 성공. providerId: {}", user.getProviderId());
                }
                break;
            case GOOGLE:
                googleClient.revoke(withdrawReq.socialAccessToken());
                break;
        }

        user.delete();

        return new WithdrawRes(user.getProvider());
    }
}
