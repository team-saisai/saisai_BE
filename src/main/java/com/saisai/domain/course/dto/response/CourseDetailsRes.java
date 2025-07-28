package com.saisai.domain.course.dto.response;

import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.course.dto.projection.CourseDetailsProjection;
import com.saisai.domain.gpx.dto.GpxPoint;
import com.saisai.domain.ride.dto.response.RideCountRes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    Long rideId,
    ChallengeStatus challengeStatus,
    LocalDate challengeEndedAt,
    Boolean isEventActive,
    List<GpxPoint> gpxPoints
) {
    public static CourseDetailsRes from(CourseDetailsProjection course, String imageUrl, RideCountRes rideCountRes, List<GpxPoint> gpxPoints, Long rideId) {

        LocalDate challengeEndedAt = Optional.ofNullable(course.challengeEndedAt())
            .map(LocalDateTime::toLocalDate)
            .orElse(null);

        return new CourseDetailsRes(
            course.id(),
            course.name(),
            course.summary(),
            course.level(),
            course.distance(),
            course.estimatedTime(),
            course.sigun(),
            imageUrl,
            rideCountRes.courseChallengerCount(),
            rideCountRes.courseFinisherCount(),
            rideId,
            course.challengeStatus(),
            challengeEndedAt,
            course.isEventActive(),
            gpxPoints
        );
    }
}
