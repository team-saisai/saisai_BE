package com.saisai.domain.ride.repository;

import com.saisai.domain.ride.entity.RideStatus;

public interface RideRepositoryCustom {

    Long countByCourseIdAndStatus(Long courseId, RideStatus status);
}
