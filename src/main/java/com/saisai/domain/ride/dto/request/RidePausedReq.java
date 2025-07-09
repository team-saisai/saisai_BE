package com.saisai.domain.ride.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;

public record RidePausedReq(
    @NotNull(message = "소요시간은 필수 입력값입니다.")
    ElapsedTimeReq elapsedTimeReq, // 소요시간 (hh:mm:ss)

    @NotNull(message = "누적 거리(km)는 필수 입력값입니다.")
    Double totalDistance, // 사용자가 지금까지 라이딩 한 총 거리

    @NotNull(message = "위도는 필수 입력값입니다.")
    Double pausedLat,

    @NotNull(message = "경도는 필수 입력값입니다.")
    Double pausedLon
) {
    public record ElapsedTimeReq(
        int hours,
        int minutes,
        int seconds
    ) {
        public Duration toDuration() {
            return Duration.ofHours(hours)
                .plusMinutes(minutes)
                .plusSeconds(seconds);
        }
    }
}
