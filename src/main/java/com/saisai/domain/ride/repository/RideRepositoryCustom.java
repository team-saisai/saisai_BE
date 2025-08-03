package com.saisai.domain.ride.repository;

import java.util.Set;

public interface RideRepositoryCustom {

    long markRideAsDeleted(Long userId, Set<Long> rideIds);
}
