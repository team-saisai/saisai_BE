package com.saisai.domain.course.repository;

import static com.saisai.domain.challenge.entity.QChallenge.challenge;
import static com.saisai.domain.course.entity.QCourse.course;
import static com.saisai.domain.reward.entity.QEventCourse.eventCourse;
import static com.saisai.domain.reward.entity.QRewardEvent.rewardEvent;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.course.dto.projection.CourseCardProjection;
import com.saisai.domain.course.dto.projection.CourseDetailsProjection;
import com.saisai.domain.course.dto.projection.CoursePageProjection;
import com.saisai.domain.course.dto.projection.QCourseCardProjection;
import com.saisai.domain.course.dto.projection.QCourseDetailsProjection;
import com.saisai.domain.course.dto.projection.QCoursePageProjection;
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

    // 전체 조회 메서드
    @Override
    public Page<CoursePageProjection> findCoursesByChallengeStatus(String challengeStatus, Pageable pageable) {

        BooleanBuilder searchConditions = searchConditions();

        if (challengeStatus != null) {
            searchConditions.and(challenge.status.eq(ChallengeStatus.valueOf(challengeStatus)));
        }

        List<CoursePageProjection> content = queryFactory
            .select(new QCoursePageProjection(
                course.id,
                course.name,
                course.level,
                course.distance,
                course.estimatedTime,
                course.sigun,
                course.image,
                challenge.status,
                challenge.endedAt,
                new QRewardEventProjection(
                    rewardEvent.id,
                    rewardEvent.status,
                    rewardEvent.type,
                    rewardEvent.value
                )
            ))
            .from(challenge)
            .join(challenge.course, course)
            .leftJoin(eventCourse).on(eventCourse.course.eq(course))
            .leftJoin(rewardEvent).on(eventCourse.rewardEvent.eq(rewardEvent))
            .where(searchConditions)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> total = queryFactory
            .select(course.countDistinct())
            .from(challenge)
            .join(challenge.course, course)
            .leftJoin(eventCourse).on(eventCourse.course.eq(course))
            .leftJoin(rewardEvent).on(eventCourse.rewardEvent.eq(rewardEvent))
            .where(searchConditions);

        return PageableExecutionUtils.getPage(content, pageable, total::fetchOne);
    }

    // 코스 Id List 기반으로 CourseCard 조회 메서드
    @Override
    public List<CourseCardProjection> findCourseCardByIds(List<Long> courseIds) {
        return queryFactory
            .select(new QCourseCardProjection(
                course.id,
                course.name,
                course.level,
                course.distance,
                course.estimatedTime,
                course.sigun,
                course.image,
                new QRewardEventProjection(
                    rewardEvent.id,
                    rewardEvent.status,
                    rewardEvent.type,
                    rewardEvent.value
                )
            ))
            .from(course)
            .leftJoin(eventCourse).on(eventCourse.course.eq(course))
            .leftJoin(rewardEvent).on(eventCourse.rewardEvent.eq(rewardEvent))
            .where(course.id.in(courseIds))
            .fetch();
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
            .leftJoin(eventCourse).on(eventCourse.course.eq(course))
            .leftJoin(eventCourse.rewardEvent, rewardEvent)
            .where(course.id.eq(courseId))
            .fetchOne();

        return Optional.ofNullable(result);
    }

    // where절 기본 정의 메서드
    private BooleanBuilder searchConditions() {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(course.isDeleted.eq(false));

        return builder;
    }
}
