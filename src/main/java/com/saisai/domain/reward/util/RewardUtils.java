package com.saisai.domain.reward.util;

import com.saisai.domain.reward.entity.RewardEventType;

public class RewardUtils {

    private RewardUtils() {}

    // 코스 레벨에 따른 기본 리워드 계산
    public static Integer calculateEventReward(Integer level) {
        if (level == null) return 0;

        return switch (level) {
            case 1 -> 10;
            case 2 -> 20;
            case 3 -> 30;
            default -> 0;
        };
    }

    // 이벤트 적용된 리워드 계산
    public static Integer calculateEventReward(Integer level, RewardEventType type, Integer value) {
        Integer baseReward = calculateEventReward(level);

        if (type == null || value == null) {
            return baseReward;
        }

        return switch (type) {
            case MULTIPLIER -> baseReward * value;
            case BONUS_FIXED -> baseReward + value;
        };
    }

}
