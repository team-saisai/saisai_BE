package com.saisai.domain.reward.service;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.reward.dto.response.UserRewardInfroRes;
import com.saisai.domain.reward.entity.UserReward;
import com.saisai.domain.reward.repository.UserRewardRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRewardService {

    private final UserRewardRepository userRewardRepository;

    // 획득 리워드 목록 조회
    @Transactional(readOnly = true)
    public UserRewardInfroRes getMyRewards(AuthUserDetails authUserDetails) {
        List<UserReward> userRewardList = userRewardRepository.findAllByUserId(authUserDetails.userId());
        Long totalReward = userRewardRepository.sumRewardByUserId(authUserDetails.userId())
            .orElse(0L);

        return UserRewardInfroRes.from(totalReward, userRewardList);
    }
}
