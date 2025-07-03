package com.saisai.domain.course.dto.response;

import com.saisai.domain.course.entity.Course;
import com.saisai.domain.gpx.dto.GpxPoint;
import com.saisai.domain.ride.dto.response.RideCountRes;
import java.util.List;

public record CourseDetailsRes(
    Long courseId,
    String courseName,
    String summary,
    Integer level,
    Double distance,
    Double estimatedTime,
    String sigun,
    String imageUrl,
    Long challengerCount,
    Long finisherCount,
    List<GpxPoint> gpxPoints
) {
    public static CourseDetailsRes from(Course course, String imageUrl, RideCountRes rideCountRes, List<GpxPoint> gpxPoints) {
        return new CourseDetailsRes(
            course.getId(),
            course.getName(),
            course.getSummary(),
            course.getLevel(),
            course.getDistance(),
            course.getEstimatedTime(),
            course.getSigun(),
            imageUrl,
            rideCountRes.courseChallengerCount(),
            rideCountRes.courseFinisherCount(),
            gpxPoints
        );
    }
}
