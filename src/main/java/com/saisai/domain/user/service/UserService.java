package com.saisai.domain.user.service;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.common.exception.ExceptionCode;
import com.saisai.domain.user.dto.response.UserGreetingRes;
import com.saisai.domain.user.entity.User;
import com.saisai.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 유저 정보 조회 (홈화면)
    public UserGreetingRes getUserGreetingInfo(AuthUserDetails authUserDetails) {
        User user = userRepository.findById(authUserDetails.userId())
            .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        return UserGreetingRes.from(user);
    }

}
