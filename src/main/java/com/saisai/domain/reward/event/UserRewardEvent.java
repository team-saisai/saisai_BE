package com.saisai.domain.reward.event;

import com.saisai.domain.reward.dto.projection.RewardInfo;

public record UserRewardEvent(Long userId, RewardInfo rewardInfo) {

    public static UserRewardEvent of (Long userId, RewardInfo rewardInfo) {
        return new UserRewardEvent(
            userId, rewardInfo);
    }
}
