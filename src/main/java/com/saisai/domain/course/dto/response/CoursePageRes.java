package com.saisai.domain.course.dto.response;

import com.saisai.domain.challenge.dto.projection.ChallengeCourseProjection;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.course.dto.projection.GeneralCourseProjection;
import java.time.LocalDate;

public record CoursePageRes(
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

    public static CoursePageRes from(ChallengeCourseProjection challengeCourseProjection, String imageUrl, boolean isEventActive, int reward) {

        return new CoursePageRes(
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

    public static CoursePageRes from(GeneralCourseProjection generalCourseProjection, String imageUrl) {
        return new CoursePageRes(
            generalCourseProjection.courseId(),
            generalCourseProjection.courseName(),
            generalCourseProjection.level(),
            generalCourseProjection.distance(),
            generalCourseProjection.estimatedTime(),
            generalCourseProjection.sigun(),
            imageUrl,
            generalCourseProjection.participantsCount(),
            generalCourseProjection.isBookmarked(),
            null,
            null,
            null,
            null
        );
    }
}
