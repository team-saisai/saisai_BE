package com.saisai.domain.ride.dto.response;

import com.saisai.domain.ride.entity.Ride;

public record RideResumeRes(
    Long rideId,
    Long durationSecond,
    Integer checkpointIdx
) {
    public static RideResumeRes from (Ride ride) {
        return new RideResumeRes(
            ride.getId(),
            ride.getDurationSecond(),
            ride.getCheckpointIdx()
        );
    }
}
