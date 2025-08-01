package com.saisai.domain.challenge.dto.response;

import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.course.dto.projection.ChallengeCourseProjection;
import java.time.LocalDate;

public record PopularChallengeCourseRes(
    Long courseId,
    String courseName,
    Integer level,
    Double distance,
    Double estimatedTime,
    String sigun,
    String imageUrl,
    Long participantsCount,
    Boolean isBookmarked,
    ChallengeStatus challengeStatus,
    LocalDate challengeEndedAt,
    Boolean isEventActive,
    Integer reward
) {

    public static PopularChallengeCourseRes from(ChallengeCourseProjection challengeCourseProjection, String imageUrl, boolean isEventActive, int reward) {

        return new PopularChallengeCourseRes(
            challengeCourseProjection.courseId(),
            challengeCourseProjection.courseName(),
            challengeCourseProjection.level(),
            challengeCourseProjection.distance(),
            challengeCourseProjection.estimatedTime(),
            challengeCourseProjection.sigun(),
            imageUrl,
            challengeCourseProjection.participantsCount(),
            challengeCourseProjection.isBookmarked(),
            challengeCourseProjection.challengeStatus(),
            challengeCourseProjection.challengeEndedAt().toLocalDate(),
            isEventActive,
            reward
        );
    }
}
