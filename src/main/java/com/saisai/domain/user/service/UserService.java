package com.saisai.domain.user.service;

import static com.saisai.domain.common.exception.ExceptionCode.NICKNAME_DUPLICATE;
import static com.saisai.domain.common.exception.ExceptionCode.USER_NOT_FOUND;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.badge.repository.UserBadgeRepository;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.common.exception.ExceptionCode;
import com.saisai.domain.reward.repository.UserRewardRepository;
import com.saisai.domain.ride.repository.RideRepository;
import com.saisai.domain.user.dto.request.ProfileImageUpdateReq;
import com.saisai.domain.user.dto.request.UserNicknameReq;
import com.saisai.domain.user.dto.response.MypageRes;
import com.saisai.domain.user.dto.response.ProfileImageRes;
import com.saisai.domain.user.dto.response.UserGreetingRes;
import com.saisai.domain.user.dto.response.UserNicknameRes;
import com.saisai.domain.user.entity.User;
import com.saisai.domain.user.repository.UserRepository;
import com.saisai.infra.aws.s3.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final ImageUtil imageUtil;
    private final UserBadgeRepository userBadgeRepository;
    private final RideRepository rideRepository;
    private final UserRewardRepository userRewardRepository;

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

    @Transactional
    public ProfileImageRes updateProfileImage(ProfileImageUpdateReq req, AuthUserDetails authUserDetails) {
        User user = userRepository.findById(authUserDetails.userId())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        String imageKey = imageUtil.upload(req.image(), "user");

        user.updateImage(imageKey);

        return new ProfileImageRes(imageUtil.getImageUrl(imageKey));
    }

    @Transactional
    public void delete(User user) {
        userBadgeRepository.deleteAll(userBadgeRepository.findAllByUser(user));
        rideRepository.deleteAll(rideRepository.findAllByUser(user));

        userRewardRepository.deleteAllByUser(user);

        userRepository.delete(user);

        log.info("회원 탈퇴 성공 id = {} provider = {} email = {}", user.getId(), user.getProvider(), user.getEmail());
    }
}
