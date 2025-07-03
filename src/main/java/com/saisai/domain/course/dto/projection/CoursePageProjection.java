package com.saisai.domain.course.dto.projection;

import com.querydsl.core.annotations.QueryProjection;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import java.time.LocalDateTime;

public record CoursePageProjection(
    Long courseId,
    String courseName,
    String summary,
    Integer level,
    Double distance,
    Double estimatedTime,
    String sigun,
    String imageUrl,
    ChallengeStatus challengeStatus,
    LocalDateTime challengeEndedAt
) {

    @QueryProjection
    public CoursePageProjection {
    }
}
