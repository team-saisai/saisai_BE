package com.saisai.domain.ride.dto.request;

import jakarta.validation.constraints.NotNull;

public record RidePausedReq(
    @NotNull(message = "소요시간(초)은 필수 입력값입니다.")
    Long duration, // 소요시간 (hh:mm:ss)

    @NotNull(message = "누적 거리(km)는 필수 입력값입니다.")
    Double totalDistance // 사용자가 지금까지 라이딩 한 총 거리
) {
}
