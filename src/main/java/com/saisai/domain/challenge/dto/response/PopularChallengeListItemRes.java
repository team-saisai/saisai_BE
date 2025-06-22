package com.saisai.domain.challenge.dto.response;

import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.course.dto.response.CourseSummaryInfo;
import java.time.LocalDateTime;


public record PopularChallengeListItemRes(
    String courseName,
    Integer level,
    Double distance,
    Double estimatedTime,
    String sigun,
    String imageUrl,
    ChallengeStatus challengeStatus,
    LocalDateTime endedAt,
    Long participantCount
) {

    public static PopularChallengeListItemRes from(
        ChallengeInfoProjection challengeInfoProjection,
        CourseSummaryInfo courseSummaryInfo
    ) {
        return new PopularChallengeListItemRes(
            challengeInfoProjection.courseName(),
            courseSummaryInfo.level(),
            courseSummaryInfo.distance(),
            courseSummaryInfo.estimatedTime(),
            courseSummaryInfo.sigun(),
            courseSummaryInfo.imageUrl(),
            challengeInfoProjection.challengeStatus(),
            challengeInfoProjection.endedAt(),
            challengeInfoProjection.participantCount()
        );
    }
}
