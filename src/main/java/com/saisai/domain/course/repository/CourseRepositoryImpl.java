package com.saisai.domain.course.repository;

import static com.saisai.domain.challenge.entity.QChallenge.challenge;
import static com.saisai.domain.course.entity.QCourse.course;
import static com.saisai.domain.reward.entity.QRewardEvent.rewardEvent;
import static com.saisai.domain.ride.entity.QRide.ride;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.course.constant.CourseSortOption;
import com.saisai.domain.course.dto.projection.CourseDetailsProjection;
import com.saisai.domain.course.dto.projection.GeneralCourseProjection;
import com.saisai.domain.course.dto.projection.QCourseDetailsProjection;
import com.saisai.domain.course.dto.projection.QGeneralCourseProjection;
import com.saisai.domain.course.entity.QCourseBookmark;
import com.saisai.domain.reward.entity.EventStatus;
import com.saisai.domain.ride.entity.QRide;
import com.saisai.domain.ride.entity.RideStatus;
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

        QCourseBookmark courseBookmarkSub = new QCourseBookmark("courseBookmarkSub");
        QRide rideSub = new QRide("rideSub");

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
                    .from(courseBookmarkSub)
                    .where(courseBookmarkSub.user.id.eq(userId)
                        .and(courseBookmarkSub.course.id.eq(course.id)))
                    .exists(),
                JPAExpressions
                    .selectOne()
                    .from(rideSub)
                    .where(
                        rideSub.course.id.eq(course.id)
                            .and(rideSub.user.id.eq(userId))
                            .and(rideSub.status.eq(RideStatus.COMPLETED))
                            .and(rideSub.isDeleted.isFalse())
                    )
                    .exists()
            ))
            .from(course)
            .leftJoin(challenge).on(
                challenge.course.id.eq(course.id)
                    .and(challenge.status.eq(ChallengeStatus.ONGOING))
            )
            .leftJoin(ride).on(ride.course.id.eq(course.id)
                .and(ride.isDeleted.isFalse()))
            .where(course.isVisible.isTrue()
                .and(course.isDeleted.eq(false))
                .and(challenge.id.isNull()))
            .groupBy(course.id, course.name, course.level, course.distance,
                course.estimatedTime, course.sigun, course.image)
            .orderBy(sortOption.toOrderSpecifier(),
                course.name.asc())
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
            .leftJoin(ride).on(ride.course.id.eq(course.id)
                .and(ride.isDeleted.isFalse()))
            .where(course.isDeleted.eq(false)
                .and(challenge.id.isNull())
                .and(course.isVisible.isTrue()));

        return PageableExecutionUtils.getPage(content, pageable, total::fetchOne);
    }

    // 코스 상세 조회
    @Override
    public Optional<CourseDetailsProjection> findCourseDetailsProjection(Long courseId, Long userId) {

        QRide rideSub = new QRide("rideSub");

        CourseDetailsProjection result = queryFactory
            .select(new QCourseDetailsProjection(
                course.id,
                course.durunubiCourseId,
                course.name,
                course.summary,
                course.level,
                course.distance,
                course.estimatedTime,
                course.sigun,
                course.image,
                course.gpxPath,
                course.checkpointGpxPath,
                challenge.status,
                challenge.endedAt,
                rewardEvent.id.isNotNull(),
                JPAExpressions
                    .selectOne()
                    .from(rideSub)
                    .where(
                        rideSub.course.id.eq(course.id)
                            .and(rideSub.user.id.eq(userId))
                            .and(rideSub.status.eq(RideStatus.COMPLETED))
                            .and(rideSub.isDeleted.isFalse())
                    )
                    .exists()
            ))
            .from(course)
            .leftJoin(challenge).on(challenge.course.eq(course)
                .and(challenge.status.eq(ChallengeStatus.ONGOING)))
            .leftJoin(rewardEvent).on(rewardEvent.challenge.eq(challenge)
                .and(rewardEvent.status.eq(EventStatus.ACTIVE)))
            .where(course.id.eq(courseId)
                .and(course.isVisible.isTrue()))
            .fetchOne();

        return Optional.ofNullable(result);
    }
}