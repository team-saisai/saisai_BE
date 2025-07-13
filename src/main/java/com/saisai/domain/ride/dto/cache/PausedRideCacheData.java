package com.saisai.domain.ride.dto.cache;

import com.saisai.domain.ride.dto.request.RidePausedReq;
import java.time.LocalDateTime;

public record PausedRideCacheData(
    Long elapsedTimeSecond,
    Double totalDistance,
    LocalDateTime pausedAt
) {
    public static PausedRideCacheData from (RidePausedReq ridePausedReq) {
        return new PausedRideCacheData(
            ridePausedReq.elapsedTimeSecond(),
            ridePausedReq.totalDistance(),
            LocalDateTime.now()
        );
    }
}
