package com.saisai.domain.ride.dto.response;

import com.saisai.domain.challenge.entity.ChallengeStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RideRecordRes(
    Long rideId,
    Long courseId,
    String courseName,
    String sigun,
    Integer level,
    LocalDateTime lastRideDate,
    Double distance,
    Long durationSecond,
    Integer progressRate,
    String imageUrl,
    Boolean isCompleted,
    ChallengeStatus challengeStatus,
    LocalDate challengeEndedAt,
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
            originalDto.durationSecond(),
            originalDto.progressRate(),
            imageUrl,
            originalDto.isCompleted(),
            originalDto.challengeStatus(),
            originalDto.challengeEndedAt(),
            originalDto.isEventActive()
        );
    }
}
