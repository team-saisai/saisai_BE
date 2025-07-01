package com.saisai.domain.course.dto.response;

import com.saisai.domain.course.entity.Course;
import com.saisai.domain.gpx.dto.GpxPoint;
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
    Integer courseChallengerCount,
    Integer courseFinisherCount,
    List<GpxPoint> gpxPoints
) {
    public static CourseDetailsRes from(Course course, String imageUrl, Long courseChallengerCount, Long courseFinisherCount, List<GpxPoint> gpxPoints) {
        return new CourseDetailsRes(
            course.getId(),
            course.getName(),
            course.getSummary(),
            course.getLevel(),
            course.getDistance(),
            course.getEstimatedTime(),
            course.getSigun(),
            imageUrl,
            courseChallengerCount != null ? courseChallengerCount.intValue() : 0,
            courseFinisherCount != null ? courseFinisherCount.intValue() : 0,
            gpxPoints
        );
    }
}
