package com.saisai.domain.user.service;

import static com.saisai.domain.common.exception.ExceptionCode.NICKNAME_DUPLICATE;
import static com.saisai.domain.common.exception.ExceptionCode.USER_NOT_FOUND;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.common.exception.ExceptionCode;
import com.saisai.domain.user.dto.request.UserNicknameReq;
import com.saisai.domain.user.dto.response.MypageRes;
import com.saisai.domain.user.dto.response.UserGreetingRes;
import com.saisai.domain.user.entity.User;
import com.saisai.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    // 유저 정보 조회 (홈화면)
    public UserGreetingRes getUserGreetingInfo(AuthUserDetails authUserDetails) {
        User user = userRepository.findById(authUserDetails.userId())
            .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        return UserGreetingRes.from(user);
    }

    // 유저 정보 조회  (마이페이지)
    public MypageRes getMypageInfo(AuthUserDetails authUserDetails) {
        return userRepository.findUserInfoById(authUserDetails.userId());
    }

    public UserNicknameRes checkNicknameDuplica(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(NICKNAME_DUPLICATE);
        }

        return new UserNicknameRes(nickname);
    }

    @Transactional
    public UserNicknameRes updateNickname(UserNicknameReq req, AuthUserDetails authUserDetails) {

        checkNicknameDuplica(req.nickname());

        User user = userRepository.findById(authUserDetails.userId())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        user.updateNickname(req.nickname());

        return new UserNicknameRes(user.getNickname());
    }
}
