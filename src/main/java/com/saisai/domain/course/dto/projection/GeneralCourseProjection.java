package com.saisai.domain.course.dto.projection;

import com.querydsl.core.annotations.QueryProjection;

public record GeneralCourseProjection(
    Long courseId,
    String courseName,
    Integer level,
    Double distance,
    Double estimatedTime,
    String sigun,
    String imageUrl,
    Long participantsCount,
    Boolean isBookmarked,
    Boolean isCompleted
) {

    @QueryProjection
    public GeneralCourseProjection {}
}
