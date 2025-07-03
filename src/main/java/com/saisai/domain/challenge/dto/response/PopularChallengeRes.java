package com.saisai.domain.challenge.dto.response;

import com.saisai.domain.challenge.dto.projection.PopularChallengeProjection;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.course.dto.projection.CourseCardProjection;
import java.time.LocalDate;


public record PopularChallengeRes(
    Long courseId,
    String courseName,
    Integer level,
    Double distance,
    Double estimatedTime,
    String sigun,
    String courseImageUrl,
    ChallengeStatus challengeStatus,
    LocalDate endedAt,
    Long challengerCount
) {

    public static PopularChallengeRes from(
        PopularChallengeProjection popularChallengeProjection,
        CourseCardProjection courseCardProjection,
        String courseImageUrl
    ) {
        return new PopularChallengeRes(
            courseCardProjection.courseId(),
            courseCardProjection.courseName(),
            courseCardProjection.level(),
            courseCardProjection.distance(),
            courseCardProjection.estimatedTime(),
            courseCardProjection.sigun(),
            courseImageUrl,
            popularChallengeProjection.challengeStatus(),
            popularChallengeProjection.endedAt().toLocalDate(),
            popularChallengeProjection.challengerCount()
        );
    }
}
