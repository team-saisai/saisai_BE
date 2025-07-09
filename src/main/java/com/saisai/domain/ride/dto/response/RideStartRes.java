package com.saisai.domain.ride.dto.response;

import com.saisai.domain.gpx.dto.GpxPoint;
import com.saisai.domain.ride.entity.Ride;
import java.util.List;

public record RideStartRes(
    Long rideId,
    Double distance,
    List<GpxPoint> gpxPoints
) {

    public static RideStartRes from(Ride ride, List<GpxPoint> gpxPoints) {
        return new RideStartRes(
            ride.getId(),
            ride.getCourse().getDistance(),
            gpxPoints
        );
    }
}
