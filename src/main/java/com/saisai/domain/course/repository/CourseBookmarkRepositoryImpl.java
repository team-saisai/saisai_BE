package com.saisai.domain.course.repository;

import static com.saisai.domain.challenge.entity.QChallenge.challenge;
import static com.saisai.domain.course.entity.QCourse.course;
import static com.saisai.domain.course.entity.QCourseBookmark.courseBookmark;
import static com.saisai.domain.reward.entity.QRewardEvent.rewardEvent;
import static com.saisai.domain.ride.entity.QRide.ride;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.course.dto.projection.CourseUnifiedProjection;
import com.saisai.domain.course.dto.projection.QCourseUnifiedProjection;
import com.saisai.domain.reward.dto.projection.QRewardEventProjection;
import com.saisai.domain.reward.entity.EventStatus;
import com.saisai.domain.ride.entity.QRide;
import com.saisai.domain.ride.entity.RideStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CourseBookmarkRepositoryImpl implements CourseBookmarkRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CourseUnifiedProjection> findByBookmarkCourses(Pageable pageable, Long userId) {

        QRide rideSub = new QRide("rideSub");

        List<CourseUnifiedProjection> content = queryFactory
            .select(new QCourseUnifiedProjection(
                course.id,
                course.name,
                course.level,
                course.distance,
                course.estimatedTime,
                course.sigun,
                course.image,
                ride.count().coalesce(0L),
                Expressions.TRUE,
                JPAExpressions
                    .selectOne()
                    .from(rideSub)
                    .where(
                        rideSub.course.id.eq(course.id)
                            .and(rideSub.user.id.eq(userId))
                            .and(rideSub.status.eq(RideStatus.COMPLETED))
                            .and(rideSub.isDeleted.isFalse())
                    )
                    .exists(),
                challenge.status,
                challenge.endedAt,
                new QRewardEventProjection(
                    rewardEvent.id,
                    rewardEvent.status,
                    rewardEvent.type,
                    rewardEvent.value
                )
            ))
            .from(courseBookmark)
            .leftJoin(course).on(courseBookmark.course.id.eq(course.id))
            .leftJoin(challenge).on(challenge.course.id.eq(course.id)
                .and(challenge.status.eq(ChallengeStatus.ONGOING)))
            .leftJoin(ride).on(ride.course.id.eq(course.id)
                .and(ride.isDeleted.isFalse()))
            .leftJoin(rewardEvent).on(
                rewardEvent.challenge.id.eq(challenge.id)
                    .and(rewardEvent.status.eq(EventStatus.ACTIVE)))
            .where(courseBookmark.user.id.eq(userId)
                .and(course.isDeleted.eq(false))
                .and(course.isVisible.isTrue()))
            .groupBy(course.id, course.name, course.level, course.distance,
                course.estimatedTime, course.sigun, course.image,
                challenge.status, challenge.endedAt,
                rewardEvent.id, rewardEvent.status, rewardEvent.type, rewardEvent.value)
            .orderBy(courseBookmark.createdAt.desc(),
                course.name.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> total = queryFactory
            .select(course.countDistinct())
            .from(courseBookmark)
            .leftJoin(courseBookmark.course, course)
            .where(courseBookmark.user.id.eq(userId)
                .and(course.isDeleted.eq(false))
                .and(course.isVisible.isTrue()));

        return PageableExecutionUtils.getPage(content, pageable, total::fetchOne);
    }
}
