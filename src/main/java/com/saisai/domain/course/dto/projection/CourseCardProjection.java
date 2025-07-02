package com.saisai.domain.course.dto.projection;

import com.querydsl.core.annotations.QueryProjection;

public record CourseCardProjection(
    Long courseId,
    String courseName,
    Integer level,
    Double distance,
    Double estimatedTime,
    String sigun,
    String image
) {

    @QueryProjection
    public CourseCardProjection {
    }
}
