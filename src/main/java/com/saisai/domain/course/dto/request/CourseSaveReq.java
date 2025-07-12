package com.saisai.domain.course.dto.request;

import jakarta.validation.constraints.NotNull;

public record CourseSaveReq(
    @NotNull(message = "코스ID는 필수 입력 값입니다.")
    Long courseId
) {

}
