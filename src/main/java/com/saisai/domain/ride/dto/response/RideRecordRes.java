package com.saisai.domain.ride.dto.response;

import com.saisai.domain.challenge.entity.ChallengeStatus;
import java.time.LocalDateTime;

public record RideRecordRes(
    Long rideId,
    Long courseId,
    String courseName,
    String sigun,
    Integer level,
    LocalDateTime lastRideDate,
    Double distance,
    Double estimatedTime,
    Integer progressRate,
    String imageUrl,
    Boolean isCompleted,
    ChallengeStatus challengeStatus,
    LocalDateTime challengeEndedAt,
    Boolean isEventActive
) {

    public static RideRecordRes from (RideRecordRes originalDto, String imageUrl) {
        return new RideRecordRes(
            originalDto.rideId(),
            originalDto.courseId(),
            originalDto.courseName(),
            originalDto.sigun(),
            originalDto.level(),
            originalDto.lastRideDate(),
            originalDto.distance(),
            originalDto.estimatedTime(),
            originalDto.progressRate(),
            imageUrl,
            originalDto.isCompleted(),
            originalDto.challengeStatus(),
            originalDto.challengeEndedAt(),
            originalDto.isEventActive()
        );
    }
}
