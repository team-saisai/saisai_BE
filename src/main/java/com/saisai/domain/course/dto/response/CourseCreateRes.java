package com.saisai.domain.course.dto.response;

import com.saisai.domain.course.entity.Course;

public record CourseCreateRes(
    Long courseId,
    String name
) {

    public static CourseCreateRes from (Course course) {
        return new CourseCreateRes(
            course.getId(),
            course.getName()
        );
    }
}
