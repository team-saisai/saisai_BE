package com.saisai.domain.reward.dto.projection;

import com.querydsl.core.annotations.QueryProjection;
import com.saisai.domain.course.entity.Course;
import com.saisai.domain.reward.entity.RewardEventType;

public record RewardInfo(
    Course cousre,
    RewardEventType rewardEventType,
    Integer value
) {

    @QueryProjection
    public RewardInfo {}

}
