package com.saisai.domain.course.dto.response;

public record CourseBookmarkRes(
    Boolean isCourseBookmarked
) {
    public static CourseBookmarkRes of(boolean isCourssBookmarked) {
        return new CourseBookmarkRes(
            isCourssBookmarked
        );
    }
}
