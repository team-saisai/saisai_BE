package com.saisai.domain.challenge.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

public record CreateChallengeReq(
    @NotNull
    Set<Long> courseIds,
    @NotNull
    LocalDateTime startedAt,
    @NotNull
    LocalDateTime closedAt
) {

}
