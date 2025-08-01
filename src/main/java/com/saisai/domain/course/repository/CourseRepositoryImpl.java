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
import com.saisai.domain.course.constant.CourseSortOption;
import com.saisai.domain.course.dto.projection.ChallengeCourseProjection;
import com.saisai.domain.course.dto.projection.CourseDetailsProjection;
import com.saisai.domain.course.dto.projection.GeneralCourseProjection;
import com.saisai.domain.course.dto.projection.QChallengeCourseProjection;
import com.saisai.domain.course.dto.projection.QCourseDetailsProjection;
import com.saisai.domain.course.dto.projection.QGeneralCourseProjection;
import com.saisai.domain.reward.dto.projection.QRewardEventProjection;
import com.saisai.domain.reward.entity.EventStatus;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CourseRepositoryImpl implements CourseRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 일반 코스 조회
    @Override
    public Page<GeneralCourseProjection> findGeneralCourses(Pageable pageable,
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
                JPAExpressions
                    .selectOne()
                    .from(courseBookmark)
                    .where(courseBookmark.user.id.eq(userId)
                        .and(courseBookmark.course.id.eq(course.id))
                    )
                    .exists()
            ))
            .from(course)
            .leftJoin(challenge).on(
                challenge.course.id.eq(course.id)
                    .and(challenge.status.eq(ChallengeStatus.ONGOING))
            )
            .leftJoin(ride).on(ride.course.id.eq(course.id))
            .where(course.isDeleted.eq(false)
                .and(challenge.id.isNull()))
            .groupBy(course.id, course.name, course.level, course.distance,
                course.estimatedTime, course.sigun, course.image)
            .orderBy(sortOption.toOrderSpecifier())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> total = queryFactory
            .select(course.countDistinct())
            .from(course)
            .leftJoin(challenge).on(
                challenge.course.id.eq(course.id)
                    .and(challenge.status.eq(ChallengeStatus.ONGOING))
            )
            .where(course.isDeleted.eq(false)
                .and(challenge.id.isNull()));

        return PageableExecutionUtils.getPage(content, pageable, total::fetchOne);
    }

    // 챌린지 코스 조회
    @Override
    public Page<ChallengeCourseProjection> findChallengeCourses(Pageable pageable,
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
                JPAExpressions
                    .selectOne()
                    .from(courseBookmark)
                    .where(courseBookmark.user.id.eq(userId)
                        .and(courseBookmark.course.id.eq(course.id))
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
            .from(course)
            .innerJoin(challenge).on(
                challenge.course.id.eq(course.id)
                    .and(challenge.status.eq(ChallengeStatus.ONGOING))
            )
            .leftJoin(ride).on(ride.course.id.eq(course.id))
            .leftJoin(rewardEvent).on(
                rewardEvent.challenge.id.eq(challenge.id)
                    .and(rewardEvent.status.eq(EventStatus.ACTIVE)))
            .where(course.isDeleted.eq(false))
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
            .innerJoin(challenge).on(
                challenge.course.id.eq(course.id)
                    .and(challenge.status.eq(ChallengeStatus.ONGOING))
            )
            .where(course.isDeleted.eq(false));

        return PageableExecutionUtils.getPage(content, pageable, total::fetchOne);
    }

    // 코스 상세 조회
    @Override
    public Optional<CourseDetailsProjection> findCourseDetailsProjection(Long courseId) {

        CourseDetailsProjection result = queryFactory
            .select(new QCourseDetailsProjection(
                course.id,
                course.name,
                course.summary,
                course.level,
                course.distance,
                course.estimatedTime,
                course.sigun,
                course.image,
                course.gpxPath,
                challenge.status,
                challenge.endedAt,
                Expressions.asBoolean(rewardEvent.status.eq(EventStatus.ACTIVE))
                    .coalesce(false)
            ))
            .from(course)
            .leftJoin(challenge).on(challenge.course.eq(course)
                .and(challenge.status.eq(ChallengeStatus.ONGOING)))
            .leftJoin(rewardEvent).on(rewardEvent.challenge.eq(challenge)) // 여기 동적 조건 추가해야할듯. 챌린지인지 아닌지.
            .where(course.id.eq(courseId))
            .fetchOne();

        return Optional.ofNullable(result);
    }
}