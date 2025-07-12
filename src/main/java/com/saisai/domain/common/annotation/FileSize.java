package com.saisai.domain.common.annotation;

import com.saisai.domain.common.valid.FileSizeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileSizeValidator.class)
public @interface FileSize {
    String message() default "파일 크기가 제한을 초과했습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    long maxSizeInMB() default 2;
}
