package com.saisai.domain.course.dto.response;

import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.course.dto.projection.CoursePageProjection;
import com.saisai.domain.reward.dto.projection.RewardEventProjection;
import com.saisai.domain.reward.util.RewardUtils;
import com.saisai.domain.ride.dto.response.RideCountRes;
import java.time.LocalDate;

public record CoursePageRes(
    Long courseId,
    String courseName,
    Integer level,
    Double distance,
    Double estimatedTime,
    String sigun,
    String imageUrl,
    Long courseChallengerCount,
    Long courseFinisherCount,
    ChallengeStatus challengeStatus,
    LocalDate challengeEndedAt,
    Boolean isEventActive,
    Integer reward
) {

    public static CoursePageRes from(CoursePageProjection coursePageProjection, RideCountRes rideCountRes, String imageUrl) {

        RewardEventProjection rewardEventProjection = coursePageProjection.rewardEventProjection();
        boolean isEventActive = rewardEventProjection.rewardEventId() != null;

        Integer reward = isEventActive ?
            RewardUtils.calculateEventReward(
                coursePageProjection.level(),
                rewardEventProjection.rewardEventType(),
                rewardEventProjection.value()) :
            RewardUtils.calculateEventReward(coursePageProjection.level());

        return new CoursePageRes(
            coursePageProjection.courseId(),
            coursePageProjection.courseName(),
            coursePageProjection.level(),
            coursePageProjection.distance(),
            coursePageProjection.estimatedTime(),
            coursePageProjection.sigun(),
            imageUrl,
            rideCountRes.courseChallengerCount(),
            rideCountRes.courseFinisherCount(),
            coursePageProjection.challengeStatus(),
            coursePageProjection.challengeEndedAt().toLocalDate(),
            isEventActive,
            reward
        );
    }
}
