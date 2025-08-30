package com.saisai.domain.ride.dto.response;

import com.saisai.infra.checkpoint.dto.response.Checkpoint;
import com.saisai.domain.course.entity.Course;
import com.saisai.infra.gpx.dto.GpxPoint;
import com.saisai.domain.ride.entity.Ride;
import java.util.List;

public record RideStartRes(
    Long rideId,
    String sigun,
    String courseName,
    Double distance,
    List<GpxPoint> gpxPoints,
    List<Checkpoint> checkpoints
) {

    public static RideStartRes from(Ride ride, Course course, List<GpxPoint> gpxPoints, List<Checkpoint> checkpoints) {
        return new RideStartRes(
            ride.getId(),
            course.getSigun(),
            course.getName(),
            ride.getCourse().getDistance(),
            gpxPoints,
            checkpoints
        );
    }
}
