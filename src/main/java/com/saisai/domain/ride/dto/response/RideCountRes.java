package com.saisai.domain.ride.dto.response;

public record RideCountRes(
    Long courseId,
    Long courseChallengerCount,
    Long courseFinisherCount
) {

    public static RideCountRes empty(Long courseId) {
        return new RideCountRes(courseId, 0L, 0L);
    }
}
