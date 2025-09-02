package com.saisai.domain.ride.repository;

import static com.saisai.domain.badge.constant.BadgeConstants.HARD_COURSE_LEVEL;
import static com.saisai.domain.challenge.entity.QChallenge.challenge;
import static com.saisai.domain.course.entity.QCourse.course;
import static com.saisai.domain.reward.entity.QRewardEvent.rewardEvent;
import static com.saisai.domain.ride.entity.QRide.ride;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.reward.entity.EventStatus;
import com.saisai.domain.ride.constant.RideSortOption;
import com.saisai.domain.ride.dto.query.RideRecordQuery;
import com.saisai.domain.ride.entity.RideStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
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

    @Override
    public Page<RideRecordQuery> findMyRideRecords(Pageable pageable, RideSortOption sortOption,
        Boolean ridingCourseOnly, Long userId) {

        BooleanExpression whereClause = ride.user.id.eq(userId)
            .and(ride.isDeleted.isFalse());

        if (ridingCourseOnly) {
            whereClause = whereClause.and(ride.status.ne(RideStatus.COMPLETED));
        }

        List<RideRecordQuery> content = jpaQueryFactory
            .select(Projections.constructor(RideRecordQuery.class,
                ride.id,
                course.id,
                course.name,
                course.sigun,
                course.level,
                ride.modifiedAt,
                course.distance,
                ride.durationSecond,
                ride.progressRate,
                course.image,
                ride.status.eq(RideStatus.COMPLETED),
                challenge.status,
                challenge.endedAt,
                new CaseBuilder()
                    .when(challenge.isNotNull()
                        .and(rewardEvent.isNotNull())
                        .and(rewardEvent.status.eq(EventStatus.ACTIVE)))
                        .then(true)
                    .when(challenge.isNotNull())
                        .then(false)
                    .otherwise((Boolean) null)
                    .as("isEventActive")
            ))
            .from(ride)
            .join(ride.course, course)
            .leftJoin(challenge).on(
                challenge.course.eq(course)
                    .and(challenge.status.eq(ChallengeStatus.ONGOING)))
            .leftJoin(rewardEvent).on(rewardEvent.challenge.eq(challenge))
            .where(whereClause
                .and(course.isVisible.isTrue()))
            .orderBy(sortOption.toOrderSpecifier())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> total = jpaQueryFactory
            .select(ride.id.count())
            .from(ride)
            .join(ride.course, course)
            .leftJoin(challenge).on(
                challenge.course.eq(course)
                    .and(challenge.status.eq(ChallengeStatus.ONGOING))
            )
            .leftJoin(rewardEvent).on(rewardEvent.challenge.eq(challenge))
            .where(whereClause
                .and(course.isVisible.isTrue()));

        return PageableExecutionUtils.getPage(content, pageable, total::fetchOne);
    }

    @Override
    public long countCompletedRides(Long userId) {
        Long count = jpaQueryFactory
            .select(ride.count())
            .from(ride)
            .where(
                ride.user.id.eq(userId)
                    .and(ride.status.eq(RideStatus.COMPLETED))
            )
            .fetchOne();

        return count != null ? count : 0L;
    }

    @Override
    public long countDistinctSigunsByUserId(Long userId) {
        Long count = jpaQueryFactory
            .select(ride.course.sigun.countDistinct())
            .from(ride)
            .where(ride.user.id.eq(userId)
                .and(ride.status.eq(RideStatus.COMPLETED))
            )
            .fetchOne();

        return count != null ? count : 0L;
    }

    @Override
    public long countCompletedHardCoursesByUserId(Long userId) {
        Long count = jpaQueryFactory
            .select(ride.count())
            .from(ride)
            .where(ride.user.id.eq(userId)
                .and(ride.status.eq(RideStatus.COMPLETED))
                .and(ride.course.level.eq(HARD_COURSE_LEVEL))
            )
            .fetchOne();

        return count != null ? count : 0L;
    }
}
