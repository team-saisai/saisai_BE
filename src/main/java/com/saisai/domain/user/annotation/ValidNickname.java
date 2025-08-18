package com.saisai.domain.user.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = {})
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)

@Pattern(regexp = "^[가-힣a-zA-Z0-9]{1,7}$", message = "이름은 1자 이상 7자 이하의 한글, 영어, 숫자만 사용할 수 있습니다.")
@NotBlank(message = "닉네임은 공백일 수 없습니다.")
@Size(min = 1, max = 10, message = "닉네임은 1자 이상 10자 이하로 입력해주세요.")
public @interface ValidNickname {

    String message() default "닉네임이 유효하지 않습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
