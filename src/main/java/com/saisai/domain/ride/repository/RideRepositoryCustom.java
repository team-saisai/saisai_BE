package com.saisai.domain.ride.repository;

import com.saisai.domain.ride.constant.RideSortOption;
import com.saisai.domain.ride.dto.response.RideRecordRes;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RideRepositoryCustom {

    long markRideAsDeleted(Long userId, Set<Long> rideIds);

    Page<RideRecordRes> findMyRideRecords(Pageable pageable, RideSortOption sortOption, Boolean inProgressOnly, Long userId);

    long countCompletedRides(Long userId);
    long countDistinctSigunsByUserId(Long userId);
    long countCompletedHardCoursesByUserId(Long userId);
}
