package com.saisai.domain.ride.repository;

import com.saisai.domain.ride.dto.response.RideCountRes;
import com.saisai.domain.ride.entity.Ride;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RideRepository extends JpaRepository<Ride, Long>, RideRepositoryCustom {

    // 최근 라이드한 코스 조회 메서드
    Ride findTop1ByUserIdOrderByModifiedAtDesc(Long id);

    // courseId와 일치하는 도전자, 완주자 수 조회 메서드 (리스트)
    @Query("""
        SELECT NEW com.saisai.domain.ride.dto.response.RideCountRes(
            r.course.id,
            COUNT(CASE WHEN r.status = 'IN_PROGRESS' THEN 1 END),
            COUNT(CASE WHEN r.status = 'COMPLETED' THEN 1 END)
        )
        FROM Ride r
        WHERE r.course.id IN :courseIds
        GROUP BY r.course.id
    """)
    List<RideCountRes> countRidesByCourseIds(@Param("courseIds") List<Long> courseIds);

    // countRidesByCourseId을 맵으로 변환하는 메서드
    default Map<Long, RideCountRes> countRidesMapByCourseIds(List<Long> courseIds) {
        return countRidesByCourseIds(courseIds).stream()
            .collect(Collectors.toMap(
                RideCountRes::courseId,
                Function.identity()
            ));
    }

    // courseId와 일치하는 도전자, 완주자 수 조회 메서드 (단건)
    @Query("""
        SELECT NEW com.saisai.domain.ride.dto.response.RideCountRes(
            r.course.id,
            COUNT(CASE WHEN r.status = 'IN_PROGRESS' THEN 1 END),
            COUNT(CASE WHEN r.status = 'COMPLETED' THEN 1 END)
        )
        FROM Ride r
        WHERE r.course.id = :courseId
    """)
    RideCountRes countRideByCourseId(@Param("courseId") Long courseId);
}
