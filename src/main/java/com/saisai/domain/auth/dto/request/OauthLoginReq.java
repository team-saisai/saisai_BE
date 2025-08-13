package com.saisai.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OauthLoginReq(
    @NotBlank(message = "토큰은 필수 입력값입니다.")
    String token
) {

}
