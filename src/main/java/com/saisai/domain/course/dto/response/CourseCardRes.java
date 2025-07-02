package com.saisai.domain.course.dto.response;

import com.saisai.domain.course.entity.Course;

public record CourseCardRes(
    String courseName,
    Integer level,
    Double distance,
    Double estimatedTime,
    String sigun,
    String imageUrl,
    Long courseChallengerCount
) {

    public static CourseCardRes from(Course course, String imageUrl, Long courseChallengerCount) {
        return new CourseCardRes(
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
