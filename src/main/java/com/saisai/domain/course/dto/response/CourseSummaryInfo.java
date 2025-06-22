package com.saisai.domain.course.dto.response;

import com.saisai.domain.course.api.CourseItem;

public record CourseSummaryInfo(
    Integer level,
    Double distance,
    Double estimatedTime,
    String sigun,
    String imageUrl
) {

    public static CourseSummaryInfo from(CourseItem courseItem, String imageUrl) {
        return new CourseSummaryInfo(
            courseItem.level(),
            courseItem.distance(),
            courseItem.estimatedTime(),
            courseItem.sigun(),
            imageUrl
        );
    }
}
