package com.saisai.domain.challenge.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

public record CreateChallengeReq(
    @NotNull
    Set<Long> courseIds,

    @Future(message = "챌린지 시작 시간은 현재 시간 이후여야 합니다.")
    @NotNull(message = "챌린지 시작 시간은 필수입니다.")
    LocalDateTime startTime,

    @Future(message = "챌린지 종료 시간은 현재 시간 이후여야 합니다.")
    @NotNull(message = "챌린지 종료 시간은 필수입니다.")
    LocalDateTime endTime
) {

    @JsonIgnore // swagger에서 숨기기 위해
    @AssertTrue(message = "시작 시간은 종료 시간보다 빨라야 합니다.")
    public boolean isValidDateRange() {
        return startTime.isBefore(endTime);
    }
}
