package com.saisai.domain.course.dto.response;

import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.course.dto.projection.ChallengeCourseProjection;
import com.saisai.domain.course.dto.projection.GeneralCourseProjection;
import com.saisai.domain.reward.dto.projection.RewardEventProjection;
import com.saisai.domain.reward.util.RewardUtils;
import java.time.LocalDate;
import java.util.Optional;

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

    public static CoursePageRes from(ChallengeCourseProjection challengeCourseProjection, String imageUrl) {

        RewardEventProjection rewardEventProjection = challengeCourseProjection.rewardEventProjection();

        boolean isEventActive = Optional.ofNullable(challengeCourseProjection.rewardEventProjection())
            .map(RewardEventProjection::rewardEventId)
            .isPresent();

        Integer reward = isEventActive ?
            RewardUtils.calculateEventReward(
                challengeCourseProjection.level(),
                rewardEventProjection.rewardEventType(),
                rewardEventProjection.value()) :
            RewardUtils.calculateEventReward(challengeCourseProjection.level());

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
