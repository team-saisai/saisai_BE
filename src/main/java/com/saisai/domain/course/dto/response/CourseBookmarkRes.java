package com.saisai.domain.course.dto.response;

public record CourseBookmarkRes(
    Boolean isCourseSaved
) {
    public static CourseBookmarkRes of() {
        return new CourseBookmarkRes(
            true
        );
    }
}
