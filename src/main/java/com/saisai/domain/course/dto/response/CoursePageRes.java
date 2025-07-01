package com.saisai.domain.course.dto.response;

import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.course.dto.projection.CoursePageProjection;
import com.saisai.domain.ride.dto.response.RideCountRes;
import java.time.LocalDate;

public record CoursePageRes(
    Long courseId,
    String courseName,
    String summary,
    Integer level,
    Double distance,
    Double estimatedTime,
    String sigun,
    String imageUrl,
    Long courseChallengerCount,
    Long courseFinisherCount,
    ChallengeStatus challengeStatus,
    LocalDate challengeEndedAt
) {

    public static CoursePageRes from(CoursePageProjection coursePageProjection, RideCountRes rideCountRes, String imageUrl) {

        return new CoursePageRes(
            coursePageProjection.courseId(),
            coursePageProjection.courseName(),
            coursePageProjection.summary(),
            coursePageProjection.level(),
            coursePageProjection.distance(),
            coursePageProjection.estimatedTime(),
            coursePageProjection.sigun(),
            imageUrl,
            rideCountRes.courseChallengerCount(),
            rideCountRes.courseFinisherCount(),
            coursePageProjection.challengeStatus(),
            coursePageProjection.challengeEndedAt().toLocalDate()
        );
    }
}
