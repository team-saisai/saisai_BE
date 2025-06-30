package com.saisai.domain.ride.dto.response;

import com.saisai.domain.course.api.CourseItem;
import com.saisai.domain.course.entity.CourseImage;
import com.saisai.domain.ride.entity.Ride;
import java.time.LocalDate;
import java.util.Optional;

public record RecentRideInfoRes(
    String courseName,
    String sigun,
    String courseImageUrl,
    Double distance,
    Double progressRate,
    LocalDate recentRideAt
) {

    public static RecentRideInfoRes from(Ride ride, CourseItem courseItem, CourseImage courseImage) {
        String courseImageUrl = Optional.ofNullable(courseImage)
            .map(CourseImage::getUrl)
            .orElse(null);

        return new RecentRideInfoRes(
            courseItem.courseName(),
            courseItem.sigun(),
            courseImageUrl,
            courseItem.distance(),
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
            null
        );
    }
}
