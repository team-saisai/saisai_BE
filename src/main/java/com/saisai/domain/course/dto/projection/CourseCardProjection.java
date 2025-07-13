package com.saisai.domain.course.dto.projection;

import com.querydsl.core.annotations.QueryProjection;
import com.saisai.domain.reward.dto.projection.RewardEventProjection;

public record CourseCardProjection(
    Long courseId,
    String courseName,
    Integer level,
    Double distance,
    Double estimatedTime,
    String sigun,
    String image,
    RewardEventProjection rewardEventProjection
) {

    @QueryProjection
    public CourseCardProjection {
    }
}