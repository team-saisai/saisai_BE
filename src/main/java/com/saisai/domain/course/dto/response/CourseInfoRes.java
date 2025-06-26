package com.saisai.domain.course.dto.response;

import com.saisai.domain.course.api.CourseItem;
import com.saisai.domain.gpx.dto.GpxPoint;
import java.util.List;

public record CourseInfoRes(
    String courseId,
    String courseName,
    String summary,
    Integer level,
    Double distance,
    Double estimatedTime,
    String sigun,
    String imageUrl,
    Integer inProgressUserCount,
    Integer completeUserCount,
    List<GpxPoint> gpxPoints
) {
    public static CourseInfoRes from(CourseItem courseItem, String imageUrl, Long inprogressUserCount, Long completeUserCount, List<GpxPoint> gpxPoints) {
        return new CourseInfoRes(
            courseItem.courseId(),
            courseItem.courseName(),
            courseItem.summary(),
            courseItem.level(),
            courseItem.distance(),
            courseItem.estimatedTime(),
            courseItem.sigun(),
            imageUrl,
            inprogressUserCount != null ? inprogressUserCount.intValue() : 0,
            completeUserCount != null ? completeUserCount.intValue() : 0,
            gpxPoints
        );
    }
}
