package com.saisai.domain.course.dto.response;

import com.saisai.domain.course.entity.Course;

public record CourseSummaryRes(
    String courseName,
    Integer level,
    Double distance,
    Double estimatedTime,
    String sigun,
    String imageUrl,
    Long courseChallengerCount
) {

    public static CourseSummaryRes from(Course course, String imageUrl, Long courseChallengerCount) {
        return new CourseSummaryRes(
            course.getName(),
            course.getLevel(),
            course.getDistance(),
            course.getEstimatedTime(),
            course.getSigun(),
            imageUrl,
            courseChallengerCount
        );
    }
}
