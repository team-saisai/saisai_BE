package com.saisai.domain.reward.service;

import static com.saisai.domain.common.exception.ExceptionCode.USER_NOT_FOUND;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.reward.dto.projection.RewardInfo;
import com.saisai.domain.reward.dto.response.UserRewardInfroRes;
import com.saisai.domain.reward.entity.UserReward;
import com.saisai.domain.reward.repository.UserRewardRepository;
import com.saisai.domain.reward.util.RewardUtils;
import com.saisai.domain.user.entity.User;
import com.saisai.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRewardService {

    private final UserRewardRepository userRewardRepository;
    private final UserRepository userRepository;


    // 획득 리워드 목록 조회
    @Transactional(readOnly = true)
    public UserRewardInfroRes getMyRewards(AuthUserDetails authUserDetails) {
        List<UserReward> userRewardList = userRewardRepository.findAllByUserId(authUserDetails.userId());
        Long totalReward = userRewardRepository.sumRewardByUserId(authUserDetails.userId())
            .orElse(0L);

        return UserRewardInfroRes.from(totalReward, userRewardList);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void earnReward(Long userId, RewardInfo rewardInfo) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Integer finalReward = RewardUtils.calculateEventReward(
            rewardInfo.cousre().getLevel(),
            rewardInfo.rewardEventType(),
            rewardInfo.value()
        );

        UserReward userReward = new UserReward(user, rewardInfo.cousre(), finalReward);
        userRewardRepository.save(userReward);
    }
}
