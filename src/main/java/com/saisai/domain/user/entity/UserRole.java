package com.saisai.domain.user.entity;

import static com.saisai.domain.common.exception.ExceptionCode.INVALID_USER_ROLE;

import com.saisai.domain.common.exception.CustomException;
import java.util.Arrays;

public enum UserRole {
    USER,
    ADMIN

    ;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
            .filter(r -> r.name().equalsIgnoreCase(role))
            .findFirst()
            .orElseThrow(() -> new CustomException(INVALID_USER_ROLE));
    }
}
