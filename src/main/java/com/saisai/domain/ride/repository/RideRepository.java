package com.saisai.domain.ride.repository;

import com.saisai.domain.ride.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideRepository extends JpaRepository<Ride, Long>, RideRepositoryCustom {

}
