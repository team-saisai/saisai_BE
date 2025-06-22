package com.saisai.domain.ride.repository;

import static com.saisai.domain.course.entity.QCourse.course;
import static com.saisai.domain.ride.entity.QRide.ride;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saisai.domain.ride.entity.RideStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RideRepositoryImpl implements RideRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Long countByCourseNameAndStatus(String courseName) {
        return jpaQueryFactory
            .select(ride.id.count())
            .from(ride)
            .where(course.name.eq(courseName)
                .and(ride.status.eq(RideStatus.COMPLETED)))
            .fetchOne();
    }
}
