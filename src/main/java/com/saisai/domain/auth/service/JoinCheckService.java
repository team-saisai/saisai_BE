package com.saisai.domain.auth.service;

import static com.saisai.domain.auth.constant.ProviderType.GOOGLE;
import static com.saisai.domain.auth.constant.ProviderType.KAKAO;

import com.saisai.domain.auth.dto.request.JoinCheckReq;
import com.saisai.domain.auth.dto.response.JoinCheckRes;
import com.saisai.domain.auth.oauth.UserInfo;
import com.saisai.domain.auth.oauth.apple.client.AppleClient;
import com.saisai.domain.auth.oauth.google.client.GoogleAndroidClient;
import com.saisai.domain.auth.oauth.google.client.GoogleIosClient;
import com.saisai.domain.auth.oauth.kakao.client.KakaoClient;
import com.saisai.domain.user.entity.User;
import com.saisai.domain.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JoinCheckService {

    private final UserRepository userRepository;
    private final KakaoClient kakaoClient;
    private final GoogleAndroidClient googleAndroidClient;
    private final GoogleIosClient googleIosClient;
    private final AppleClient appleClient;

    @Transactional(readOnly = true)
    public JoinCheckRes checkJoinForKakao(JoinCheckReq joinCheckReq) {
        UserInfo userInfo = kakaoClient.getUserInfo(joinCheckReq.token());

        log.info("회원가입 확인 요청. Provider: {}, ProviderId: {}, Email: {}",
            KAKAO, userInfo.providerId(), userInfo.email());

        Optional<User> optionalUser = userRepository.findByProviderId(userInfo.providerId());
        boolean isNewUser = optionalUser.isEmpty();

        return new JoinCheckRes(isNewUser);
    }

    @Transactional(readOnly = true)
    public JoinCheckRes checkJoinForGooleAndroid(JoinCheckReq joinCheckReq) {
        UserInfo userInfo = googleAndroidClient.verifyAndGetUserInfo(joinCheckReq.token());

        log.info("회원가입 확인 요청. Provider: {}, ProviderId: {}, Email: {}",
            GOOGLE, userInfo.providerId(), userInfo.email());

        Optional<User> optionalUser = userRepository.findByProviderId(userInfo.providerId());
        boolean isNewUser = optionalUser.isEmpty();

        return new JoinCheckRes(isNewUser);
    }

    @Transactional(readOnly = true)
    public JoinCheckRes checkJoinForGooleIos(JoinCheckReq joinCheckReq) {
        UserInfo userInfo = googleIosClient.verifyAndGetUserInfo(joinCheckReq.token());

        log.info("회원가입 확인 요청. Provider: {}, ProviderId: {}, Email: {}",
            GOOGLE, userInfo.providerId(), userInfo.email());

        Optional<User> optionalUser = userRepository.findByProviderId(userInfo.providerId());
        boolean isNewUser = optionalUser.isEmpty();

        return new JoinCheckRes(isNewUser);
    }

    @Transactional(readOnly = true)
    public JoinCheckRes checkJoinForApple(JoinCheckReq joinCheckReq) {
        UserInfo userInfo = appleClient.getUserInfo(joinCheckReq.token());

        log.info("회원가입 확인 요청. Provider: {}, ProviderId: {}, Email: {}",
            GOOGLE, userInfo.providerId(), userInfo.email());

        Optional<User> optionalUser = userRepository.findByProviderId(userInfo.providerId());
        boolean isNewUser = optionalUser.isEmpty();

        return new JoinCheckRes(isNewUser);
    }
}
