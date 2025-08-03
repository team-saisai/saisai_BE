package com.saisai.domain.ride.repository;

import static com.saisai.domain.ride.entity.QRide.ride;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RideRepositoryImpl implements RideRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public long markRideAsDeleted(Long userId, Set<Long> rideIds) {
        return jpaQueryFactory
            .update(ride)
            .set(ride.isDeleted, true)
            .set(ride.deletedAt, Expressions.dateTimeTemplate(LocalDateTime.class, "CURRENT_TIMESTAMP")) // DB 서버 시간 가져옴
            .where(
                ride.user.id.eq(userId),
                ride.id.in(rideIds),
                ride.isDeleted.eq(false)
            )
            .execute();
    }
}
