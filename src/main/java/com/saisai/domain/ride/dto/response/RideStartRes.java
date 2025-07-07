package com.saisai.domain.ride.dto.response;

import com.saisai.domain.gpx.dto.GpxPoint;
import java.util.List;

public record RideStartRes(
    Double distance,
    List<GpxPoint> gpxPoints
) {

    public static RideStartRes from(Double distance, List<GpxPoint> gpxPoints) {
        return new RideStartRes(
            distance,
            gpxPoints
        );
    }
}
