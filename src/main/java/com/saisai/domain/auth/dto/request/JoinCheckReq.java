package com.saisai.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record JoinCheckReq(
    @NotBlank
    String token
) {

}
