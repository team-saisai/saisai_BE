package com.saisai.domain.challenge.dto.response;

import com.saisai.domain.challenge.dto.projection.ChallengeInfoProjection;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.course.dto.response.CourseSummaryRes;
import java.time.LocalDateTime;


public record PopularChallengeListItemRes(
    Long courseId,
    String courseName,
    Integer level,
    Double distance,
    Double estimatedTime,
    String sigun,
    String imageUrl,
    ChallengeStatus challengeStatus,
    LocalDateTime endedAt,
    Long courseChallengerCount
) {

    public static PopularChallengeListItemRes from(
        ChallengeInfoProjection challengeInfoProjection,
        CourseSummaryRes courseSummaryRes
    ) {
        return new PopularChallengeListItemRes(
            challengeInfoProjection.courseId(),
            courseSummaryRes.courseName(),
            courseSummaryRes.level(),
            courseSummaryRes.distance(),
            courseSummaryRes.estimatedTime(),
            courseSummaryRes.sigun(),
            courseSummaryRes.imageUrl(),
            challengeInfoProjection.challengeStatus(),
            challengeInfoProjection.endedAt(),
            courseSummaryRes.courseChallengerCount()
        );
    }
}
