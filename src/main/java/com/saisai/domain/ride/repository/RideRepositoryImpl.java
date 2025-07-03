package com.saisai.domain.ride.repository;

import static com.saisai.domain.ride.entity.QRide.ride;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saisai.domain.ride.entity.RideStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RideRepositoryImpl implements RideRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    // courseId 기준, Ride 상태 기반 사용자 수 반환 메서드
    @Override
    public Long countByCourseIdAndStatus(Long courseId, RideStatus status) {
        return jpaQueryFactory
            .select(ride.id.count())
            .from(ride)
            .where(ride.course.id.eq(courseId)
                .and(ride.status.eq(status)))
            .fetchOne();
    }
}
