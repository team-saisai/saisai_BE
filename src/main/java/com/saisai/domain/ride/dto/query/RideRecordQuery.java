package com.saisai.domain.ride.dto.query;

import com.saisai.domain.challenge.entity.ChallengeStatus;
import java.time.LocalDateTime;

public record RideRecordQuery(
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
    LocalDateTime challengeEndedAt,
    Boolean isEventActive
) {

}
