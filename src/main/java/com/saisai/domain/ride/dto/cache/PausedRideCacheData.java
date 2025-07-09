package com.saisai.domain.ride.dto.cache;

import com.saisai.domain.ride.dto.request.RidePausedReq;
import java.time.Duration;
import java.time.LocalDateTime;

public record PausedRideCacheData(
    Duration elapsedTime,
    Double totalDistance,
    Double pausedLat,
    Double pausedLon,
    LocalDateTime pausedAt
) {
    public static PausedRideCacheData from (RidePausedReq ridePausedReq) {
        return new PausedRideCacheData(
            ridePausedReq.elapsedTimeReq().toDuration(),
            ridePausedReq.totalDistance(),
            ridePausedReq.pausedLat(),
            ridePausedReq.pausedLon(),
            LocalDateTime.now()
        );
    }
}
