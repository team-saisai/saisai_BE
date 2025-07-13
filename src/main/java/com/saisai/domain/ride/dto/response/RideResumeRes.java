package com.saisai.domain.ride.dto.response;

import com.saisai.domain.ride.entity.Ride;

public record RideResumeRes(
    Long rideId
) {
    public static RideResumeRes from (Ride ride) {
        return new RideResumeRes(
            ride.getId()
        );
    }
}
