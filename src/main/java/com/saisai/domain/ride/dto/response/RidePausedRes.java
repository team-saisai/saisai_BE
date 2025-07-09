package com.saisai.domain.ride.dto.response;

import com.saisai.domain.ride.entity.Ride;

public record RidePausedRes(
    Long rideId,
    Integer progressRate
) {
    public static RidePausedRes from (Ride ride, int progressRate) {
        return new RidePausedRes(
            ride.getId(),
            progressRate
        );
    }
}
