package com.saisai.domain.auth.constant;

import static com.saisai.domain.common.exception.ExceptionCode.INVALID_PROVIDER_TYPE;

import com.saisai.domain.common.exception.CustomException;
import java.util.Arrays;

public enum ProviderType {
    GOOGLE,
    KAKAO,
    APPLE,
    GENERAL,

    ;

    public static ProviderType of(String type) {
        return Arrays.stream(ProviderType.values())
            .filter(r -> r.name().equalsIgnoreCase(type))
            .findFirst()
            .orElseThrow(() -> new CustomException(INVALID_PROVIDER_TYPE));
    }

}
