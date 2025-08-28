package com.saisai.domain.course.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record CourseCreateReq(
    String name,
    String sigun,
    Double distance,
    Double estimatedTime,
    Integer level,
    String summary,
    MultipartFile gpxFile
) {

}
