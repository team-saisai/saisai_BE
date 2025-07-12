package com.saisai.domain.course.dto.response;

import com.saisai.domain.course.entity.CourseSave;

public record CourseSaveRes(
    Long courseSaveId,
    Boolean isCourseSaved
) {
    public static CourseSaveRes from(CourseSave courseSave) {
        return new CourseSaveRes (
            courseSave.getId(),
            true
        );
    }
}
