package com.saisai.domain.ride.service;

import com.saisai.domain.ride.dto.cache.PausedRideCacheData;
import com.saisai.domain.ride.dto.request.RidePausedReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CacheRideService {

    @CachePut(value = "pausedRideCacheData", key = "#userId + '_' + #rideId")
    public PausedRideCacheData savePausedData(Long userId, Long rideId, RidePausedReq ridePausedReq) {
        String cacheKey = userId + "_" + rideId;
        PausedRideCacheData cacheData = PausedRideCacheData.from(ridePausedReq);

        log.info("CacheKey: {}", cacheKey);
        log.info("CacheData: {}", cacheData);

        return cacheData;
    }
}
