package com.saisai.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterReq(
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    String email,

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    String nickname,

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    String password,

    @NotBlank(message = "유저롤은 필수 입력값입니다.")
    String role
) {
}
