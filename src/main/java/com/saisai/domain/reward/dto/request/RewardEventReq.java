package com.saisai.domain.reward.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saisai.domain.reward.entity.RewardEventType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record RewardEventReq(
    @NotEmpty(message = "최소 하나의 코스는 선택해야 합니다.")
    List<Long> courseIds,

    @Future(message = "이벤트 시작 시간은 현재 시간 이후여야 합니다.")
    @NotNull(message = "이벤트 시작 시간은 필수입니다.")
    LocalDateTime startTime,

    @Future(message = "이벤트 종료 시간은 현재 시간 이후여야 합니다.")
    @NotNull(message = "이벤트 종료 시간은 필수입니다.")
    LocalDateTime endTime,

    @Min(value = 1, message = "이벤트 값은 1 이상이어야 합니다.")
    @NotNull(message = "이벤트 값은 필수입니다.")
    Integer value,

    String type
) {
    public RewardEventReq {
        if (type == null || type.isBlank()) {
            type = "MULTIPLIER";  // 기본값 설정
        }
    }

    @JsonIgnore // swagger에서 숨기기 위해
    @AssertTrue(message = "시작 시간은 종료 시간보다 빨라야 합니다.")
    public boolean isValidDateRange() {
        return startTime.isBefore(endTime);
    }

    public RewardEventType getType() {
        return RewardEventType.valueOf(type.toUpperCase());
    }
}
