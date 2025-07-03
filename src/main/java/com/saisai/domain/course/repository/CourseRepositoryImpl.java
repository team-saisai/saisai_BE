package com.saisai.domain.course.repository;

import static com.saisai.domain.challenge.entity.QChallenge.challenge;
import static com.saisai.domain.course.entity.QCourse.course;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.course.dto.projection.CourseCardProjection;
import com.saisai.domain.course.dto.projection.CoursePageProjection;
import com.saisai.domain.course.dto.projection.QCourseCardProjection;
import com.saisai.domain.course.dto.projection.QCoursePageProjection;
import java.util.List;
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
                course.summary,
                course.level,
                course.distance,
                course.estimatedTime,
                course.sigun,
                course.image,
                challenge.status,
                challenge.endedAt
                ))
            .from(challenge)
            .join(challenge.course, course)
            .where(searchConditions)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> total = queryFactory
            .select(course.countDistinct())
            .from(challenge)
            .join(challenge.course, course)
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
                course.image
            ))
            .from(course)
            .where(course.id.in(courseIds))
            .fetch();
    }

    // where절 기본 정의 메서드
    private BooleanBuilder searchConditions() {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(course.isDeleted.eq(false));

        return builder;
    }
}
