package com.saisai.domain.common.utils;

import static com.saisai.domain.common.exception.ExceptionCode.INVALID_DOUBLE_FORMAT;
import static com.saisai.domain.common.exception.ExceptionCode.INVALID_INTEGER_FORMAT;

import com.saisai.domain.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParsingUtils {

    private ParsingUtils() {}

    public static Integer safeParseInteger(String str) {
        try {
            if (str == null || str.trim().isEmpty()) {
                return null;
            }
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            log.warn("정수형 변환 실패: '{}'", str, e);
            throw new CustomException(INVALID_INTEGER_FORMAT);
        }
    }

    public static Double safeParseDouble(String str) {
        try {
            if (str == null || str.trim().isEmpty()) {
                return null;
            }
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            log.warn("실수형 변환 실패: '{}'", str, e);
            throw new CustomException(INVALID_DOUBLE_FORMAT);
        }
    }
}
