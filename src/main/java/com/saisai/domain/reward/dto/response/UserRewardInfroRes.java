package com.saisai.domain.reward.dto.response;

import com.saisai.domain.reward.entity.UserReward;
import java.time.LocalDateTime;
import java.util.List;

public record UserRewardInfroRes(
    Long totalReward,
    List<RewardInfo> rewardInfos
) {
    public static UserRewardInfroRes from (Long totalReward, List<UserReward> userRewards) {
        return new UserRewardInfroRes(
            totalReward,
            RewardInfo.convert(userRewards)
        );
    }

    private record RewardInfo(
        Integer reward,
        LocalDateTime acquiredAt,
        String courseName
    ) {
        private static RewardInfo from(UserReward userReward) {
            return new RewardInfo(
                userReward.getReward(),
                userReward.getCreatedAt(),
                userReward.getCourse().getName()
            );
        }

        private static List<RewardInfo> convert (List<UserReward> userRewards) {
            return userRewards.stream()
                .map(RewardInfo::from)
                .toList();
        }
    }
}
