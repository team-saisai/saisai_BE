package com.saisai.domain.ride.repository;

import com.saisai.domain.ride.dto.response.RideCountRes;
import com.saisai.domain.ride.dto.response.RideResumeRes;
import com.saisai.domain.ride.entity.Ride;
import com.saisai.domain.ride.entity.RideStatus;
import com.saisai.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RideRepository extends JpaRepository<Ride, Long>, RideRepositoryCustom {

    // 최근 라이드한 코스 조회 메서드
    Ride findTop1ByUserIdOrderByModifiedAtDesc(Long id);

    // courseId와 일치하는 도전자, 완주자 수 조회 메서드 (단건)
    @Query("""
        SELECT NEW com.saisai.domain.ride.dto.response.RideCountRes(
            r.course.id,
            COUNT(CASE WHEN r.status = 'IN_PROGRESS' THEN 1 END),
            COUNT(CASE WHEN r.status = 'COMPLETED' THEN 1 END)
        )
        FROM Ride r
        WHERE r.course.id = :courseId and r.isDeleted = false
    """)
    RideCountRes countRideByCourseId(@Param("courseId") Long courseId);

    Boolean existsByUserIdAndStatus(Long userId, RideStatus status);

    @Query(""" 
        SELECT NEW com.saisai.domain.ride.dto.response.RideResumeRes(
            r.id,
            r.durationSecond,
            r.checkpointIdx
        )
        FROM Ride r
        WHERE r.user.id = :userId
        AND r.course.id = :courseId
        AND r.status NOT IN ('COMPLETED')
        AND r.isDeleted = false
        ORDER BY r.modifiedAt DESC
        LIMIT 1
    """)
    Optional<RideResumeRes> findActiveRideIdByUserIdAndCourseId(@Param("userId") Long userId,
        @Param("courseId") Long courseId);

    Optional<Ride> findByUserIdAndCourseIdAndStatus(Long userId, Long courseId, RideStatus rideStatus);

    List<Ride> findAllByUser(User user);
}
