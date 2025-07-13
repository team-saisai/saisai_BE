package com.saisai.domain.reward.dto.projection;

import com.querydsl.core.annotations.QueryProjection;
import com.saisai.domain.reward.entity.EventStatus;
import com.saisai.domain.reward.entity.RewardEventType;

public record RewardEventProjection(
    Long rewardEventId,
    EventStatus eventStatus,
    RewardEventType rewardEventType,
    Integer value
) {
    @QueryProjection
    public RewardEventProjection{

    }
}
