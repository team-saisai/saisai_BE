package com.saisai.domain.course.dto.response;

import com.saisai.domain.challenge.dto.projection.ChallengeCardProjection;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.course.dto.projection.CourseCardProjection;
import com.saisai.domain.reward.dto.projection.RewardEventProjection;
import com.saisai.domain.reward.util.RewardUtils;
import java.time.LocalDate;

public record CourseCardRes(
    Long courseId,
    String courseName,
    Integer level,
    Double distance,
    Double estimatedTime,
    String sigun,
    String courseImageUrl,
    ChallengeStatus challengeStatus,
    LocalDate endedAt,
    Long challengerCount,
    Boolean isEventActive,
    Integer reward
) {

    public static CourseCardRes from(
        ChallengeCardProjection challengeCardProjection,
        CourseCardProjection courseCardProjection,
        String courseImageUrl
    ) {

        RewardEventProjection rewardEventProjection = courseCardProjection.rewardEventProjection();
        boolean isEventActive = rewardEventProjection != null;

        Integer reward = isEventActive ?
            RewardUtils.calculateEventReward(
                courseCardProjection.level(),
                rewardEventProjection.rewardEventType(),
                rewardEventProjection.value()) :
            RewardUtils.calculateEventReward(courseCardProjection.level());

        return new CourseCardRes(
            courseCardProjection.courseId(),
            courseCardProjection.courseName(),
            courseCardProjection.level(),
            courseCardProjection.distance(),
            courseCardProjection.estimatedTime(),
            courseCardProjection.sigun(),
            courseImageUrl,
            challengeCardProjection.challengeStatus(),
            challengeCardProjection.endedAt().toLocalDate(),
            challengeCardProjection.challengerCount(),
            isEventActive,
            reward
        );
    }
}
