package com.saisai.domain.auth.service;

import static com.saisai.domain.common.exception.ExceptionCode.AUTH_FAILED;
import static com.saisai.domain.common.exception.ExceptionCode.EMAIL_DUPLICATE;
import static com.saisai.domain.common.exception.ExceptionCode.INVALID_REFRESH_TOKEN;
import static com.saisai.domain.common.exception.ExceptionCode.USER_NOT_FOUND;

import com.saisai.config.jwt.JwtProvider;
import com.saisai.domain.auth.dto.request.LoginReq;
import com.saisai.domain.auth.dto.request.RegisterReq;
import com.saisai.domain.auth.dto.response.TokenRes;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.common.utils.PasswordEncoder;
import com.saisai.domain.user.entity.User;
import com.saisai.domain.user.entity.UserRole;
import com.saisai.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRedisService refreshTokenRedisService;

    @Transactional
    public TokenRes register(RegisterReq registerReq) {
         if (userRepository.existsByEmail(registerReq.email())) {
             throw new CustomException(EMAIL_DUPLICATE);
         }

        String encodedPassword = passwordEncoder.encode(registerReq.password());

        User savedUser = userRepository.save(
            User.builder()
                .email(registerReq.email())
                .nickname(registerReq.nickname())
                .password(encodedPassword)
                .role(UserRole.of(registerReq.role()))
                .build()
        );

        return issueAndSaveTokens(savedUser);
    }

    // 액세스 토큰, 리프레시 토큰 발급하고 리프레시 토큰을 저장하는 메서드
    private TokenRes issueAndSaveTokens(User user) {
        String newAccessToken = jwtProvider.generateAccessToken(user);
        String newRefreshToken = jwtProvider.generateRefreshToken(user);

        refreshTokenRedisService.saveRefreshToken(user.getId(), jwtProvider.substringToken(newRefreshToken));

        return TokenRes.from(newAccessToken, newRefreshToken);
    }
}
