package com.saisai.domain.course.dto.projection;

import com.querydsl.core.annotations.QueryProjection;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.reward.dto.projection.RewardEventProjection;
import java.time.LocalDateTime;

public record CourseUnifiedProjection(
    Long courseId,
    String courseName,
    Integer level,
    Double distance,
    Double estimatedTime,
    String sigun,
    String imageUrl,
    Long participantsCount,
    Boolean isBookmarked,
    Boolean isCompleted,
    ChallengeStatus challengeStatus,
    LocalDateTime challengeEndedAt,
    RewardEventProjection rewardEventProjection
) {
    @QueryProjection
    public CourseUnifiedProjection {}
}
