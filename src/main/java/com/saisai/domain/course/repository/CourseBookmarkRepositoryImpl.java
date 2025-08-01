package com.saisai.domain.course.repository;

import static com.saisai.domain.challenge.entity.QChallenge.challenge;
import static com.saisai.domain.course.entity.QCourse.course;
import static com.saisai.domain.course.entity.QCourseBookmark.courseBookmark;
import static com.saisai.domain.reward.entity.QRewardEvent.rewardEvent;
import static com.saisai.domain.ride.entity.QRide.ride;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.course.constant.CourseSortOption;
import com.saisai.domain.course.dto.projection.ChallengeCourseProjection;
import com.saisai.domain.course.dto.projection.GeneralCourseProjection;
import com.saisai.domain.course.dto.projection.QChallengeCourseProjection;
import com.saisai.domain.course.dto.projection.QGeneralCourseProjection;
import com.saisai.domain.reward.dto.projection.QRewardEventProjection;
import com.saisai.domain.reward.entity.EventStatus;
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

    // 북마크한 챌린지 코스 조회
    @Override
    public Page<ChallengeCourseProjection> findChallengeBookmarkCourses(Pageable pageable,
        CourseSortOption sortOption, Long userId) {
        List<ChallengeCourseProjection> content = queryFactory
            .select(new QChallengeCourseProjection(
                course.id,
                course.name,
                course.level,
                course.distance,
                course.estimatedTime,
                course.sigun,
                course.image,
                ride.count().coalesce(0L),
                Expressions.TRUE,
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
            .innerJoin(courseBookmark.course, course)
            .innerJoin(challenge).on(
                challenge.course.id.eq(course.id)
                    .and(challenge.status.eq(ChallengeStatus.ONGOING))
            )
            .leftJoin(ride).on(ride.course.id.eq(course.id))
            .leftJoin(rewardEvent).on(
                rewardEvent.challenge.id.eq(challenge.id)
                    .and(rewardEvent.status.eq(EventStatus.ACTIVE)))
            .where(courseBookmark.user.id.eq(userId)
                .and(course.isDeleted.eq(false)))
            .groupBy(course.id, course.name, course.level, course.distance,
                course.estimatedTime, course.sigun, course.image,
                challenge.status, challenge.endedAt,
                rewardEvent.id, rewardEvent.status, rewardEvent.type, rewardEvent.value)
            .orderBy(sortOption.toOrderSpecifier())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> total = queryFactory
            .select(course.countDistinct())
            .from(course)
            .innerJoin(courseBookmark.course, course)
            .innerJoin(challenge).on(
                challenge.course.id.eq(course.id)
                    .and(challenge.status.eq(ChallengeStatus.ONGOING))
            )
            .where(courseBookmark.user.id.eq(userId)
                .and(course.isDeleted.eq(false)));

        return PageableExecutionUtils.getPage(content, pageable, total::fetchOne);
    }

    // 북마크한 일반 코스 조회
    @Override
    public Page<GeneralCourseProjection> findGeneralBookmarkCourses(Pageable pageable,
        CourseSortOption sortOption, Long userId) {
        List<GeneralCourseProjection> content = queryFactory
            .select(new QGeneralCourseProjection(
                course.id,
                course.name,
                course.level,
                course.distance,
                course.estimatedTime,
                course.sigun,
                course.image,
                ride.count().coalesce(0L),
                Expressions.TRUE
            ))
            .from(courseBookmark)
            .innerJoin(courseBookmark.course, course)
            .leftJoin(challenge).on(
                challenge.course.id.eq(course.id)
                    .and(challenge.status.eq(ChallengeStatus.ONGOING))
            )
            .leftJoin(ride).on(ride.course.id.eq(course.id))
            .where(courseBookmark.user.id.eq(userId)
                .and(course.isDeleted.eq(false))
                .and(challenge.id.isNull()))
            .groupBy(course.id, course.name, course.level, course.distance,
                course.estimatedTime, course.sigun, course.image)
            .orderBy(sortOption.toOrderSpecifier())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> query = queryFactory
            .select(courseBookmark.countDistinct())
            .from(courseBookmark)
            .innerJoin(courseBookmark.course, course)
            .leftJoin(challenge).on(
                challenge.course.id.eq(course.id)
                    .and(challenge.status.eq(ChallengeStatus.ONGOING))
            )
            .where(courseBookmark.user.id.eq(userId)
                .and(course.isDeleted.eq(false))
                .and(challenge.id.isNull()));

        return PageableExecutionUtils.getPage(content, pageable, query::fetchOne);
    }
}
