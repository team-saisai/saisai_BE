package com.saisai.domain.course.dto.response;

import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.infra.checkpoint.dto.response.Checkpoint;
import com.saisai.domain.course.dto.projection.CourseDetailsProjection;
import com.saisai.infra.gpx.dto.GpxPoint;
import com.saisai.domain.ride.dto.response.RideCountRes;
import com.saisai.domain.ride.dto.response.RideResumeRes;
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
    Long durationSecond,
    Integer checkpointIdx,
    Boolean isCompleted,
    ChallengeStatus challengeStatus,
    LocalDate challengeEndedAt,
    Boolean isEventActive,
    List<GpxPoint> gpxPoints,
    List<Checkpoint> checkpoint
) {
    public static CourseDetailsRes from(CourseDetailsProjection course, String imageUrl, RideCountRes rideCountRes, List<GpxPoint> gpxPoints, List<Checkpoint> checkpoint, Optional<RideResumeRes> optionalRide) {

        LocalDate challengeEndedAt = Optional.ofNullable(course.challengeEndedAt())
            .map(LocalDateTime::toLocalDate)
            .orElse(null);

        Long rideId = optionalRide.map(RideResumeRes::rideId).orElse(null);
        Long durationSecond = optionalRide.map(RideResumeRes::durationSecond).orElse(null);
        Integer checkpointIdx = optionalRide.map(RideResumeRes::checkpointIdx).orElse(null);

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
            durationSecond,
            checkpointIdx,
            course.isCompleted(),
            course.challengeStatus(),
            challengeEndedAt,
            course.isEventActive(),
            gpxPoints,
            checkpoint
        );
    }
}
