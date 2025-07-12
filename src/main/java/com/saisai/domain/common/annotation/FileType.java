package com.saisai.domain.common.annotation;

import com.saisai.domain.common.valid.FileTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileTypeValidator.class)
public @interface FileType {
    String message() default "지원하지 않는 파일 형식입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String[] allowed();
}
