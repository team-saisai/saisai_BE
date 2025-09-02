package com.saisai.domain.ride.dto.response;

import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.ride.dto.query.RideRecordQuery;
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

    public static RideRecordRes from (RideRecordQuery query, String imageUrl) {

        LocalDate challengeEndedDate = (query.challengeEndedAt() == null)
            ? null
            : query.challengeEndedAt().toLocalDate();

        return new RideRecordRes(
            query.rideId(),
            query.courseId(),
            query.courseName(),
            query.sigun(),
            query.level(),
            query.lastRideDate(),
            query.distance(),
            query.durationSecond(),
            query.progressRate(),
            imageUrl,
            query.isCompleted(),
            query.challengeStatus(),
            challengeEndedDate,
            query.isEventActive()
        );
    }
}
