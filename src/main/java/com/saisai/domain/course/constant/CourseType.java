package com.saisai.domain.course.constant;

import static com.saisai.domain.common.exception.ExceptionCode.COURSE_TYPE_NOT_FOUND;

import com.saisai.domain.common.exception.CustomException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CourseType {
    CHALLENGE("challenge"),
    GENERAL("general"),

    ;

    private final String value;

    public static CourseType from(String value) {
        return Arrays.stream(values())
            .filter(type -> type.value.equals(value))
            .findFirst()
            .orElseThrow(() -> new CustomException(COURSE_TYPE_NOT_FOUND));
    }
}
