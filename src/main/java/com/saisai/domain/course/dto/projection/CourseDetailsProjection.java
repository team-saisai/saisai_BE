package com.saisai.domain.course.dto.projection;

import com.querydsl.core.annotations.QueryProjection;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import java.time.LocalDateTime;

public record CourseDetailsProjection(
    Long id,
    String name,
    String summary,
    Integer level,
    Double distance,
    Double estimatedTime,
    String sigun,
    String imageUrl,
    String gpxpath,
    ChallengeStatus challengeStatus,
    LocalDateTime challengeEndedAt,
    Boolean isEventActive
) {
    @QueryProjection
    public CourseDetailsProjection {}
}
