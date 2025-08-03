package com.saisai.domain.ride.dto.request;

import java.util.Set;

public record RideDeleteReq(
    Set<Long> rideIds
) {
}
