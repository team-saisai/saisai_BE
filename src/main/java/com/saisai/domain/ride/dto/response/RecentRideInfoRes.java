package com.saisai.domain.ride.dto.response;

import com.saisai.domain.course.entity.Course;
import com.saisai.domain.ride.entity.Ride;
import java.time.LocalDate;

public record RecentRideInfoRes(
    Long courseId,
    String courseName,
    String sigun,
    String courseImageUrl,
    Double distance,
    Double progressRate,
    LocalDate recentRideAt
) {

    public static RecentRideInfoRes from(Ride ride, Course course, String courseImageUrl) {

        return new RecentRideInfoRes(
            course.getId(),
            course.getName(),
            course.getSigun(),
            courseImageUrl,
            course.getDistance(),
            ride.getProgressRate(),
            ride.getModifiedAt().toLocalDate()
        );
    }

    public static RecentRideInfoRes empty() {
        return new RecentRideInfoRes(
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
    }
}
