package com.saisai.domain.challenge.repository;

import static com.saisai.domain.challenge.entity.QChallenge.challenge;
import static com.saisai.domain.course.entity.QCourse.course;
import static com.saisai.domain.reward.entity.QRewardEvent.rewardEvent;
import static com.saisai.domain.ride.entity.QRide.ride;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saisai.domain.challenge.dto.projection.ChallengeCourseProjection;
import com.saisai.domain.challenge.dto.projection.QChallengeCourseProjection;
import com.saisai.domain.challenge.entity.Challenge;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.course.constant.CourseSortOption;
import com.saisai.domain.course.entity.Course;
import com.saisai.domain.course.entity.QCourseBookmark;
import com.saisai.domain.reward.dto.projection.QRewardEventProjection;
import com.saisai.domain.reward.entity.EventStatus;
import com.saisai.domain.ride.entity.QRide;
import com.saisai.domain.ride.entity.RideStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChallengeRepositoryImpl implements ChallengeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 챌린지 코스 조회
    @Override
    public Page<ChallengeCourseProjection> findChallengeCourses(Pageable pageable,
        CourseSortOption sortOption, Long userId) {

        QCourseBookmark courseBookmarkSub = new QCourseBookmark("courseBookmarkSub");
        QRide rideSub = new QRide("rideSub");

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
            .from(challenge)
            .innerJoin(challenge.course, course)
            .leftJoin(ride).on(ride.course.id.eq(course.id)
                .and(ride.isDeleted.isFalse()))
            .leftJoin(rewardEvent).on(
                rewardEvent.challenge.id.eq(challenge.id)
                    .and(rewardEvent.status.eq(EventStatus.ACTIVE)))
            .where(course.isDeleted.eq(false)
                .and(challenge.status.eq(ChallengeStatus.ONGOING)))
            .groupBy(course.id, course.name, course.level, course.distance,
                course.estimatedTime, course.sigun, course.image,
                challenge.status, challenge.endedAt,
                rewardEvent.id, rewardEvent.status, rewardEvent.type, rewardEvent.value)
            .orderBy(sortOption.toOrderSpecifier(),
                course.name.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> total = queryFactory
            .select(course.countDistinct())
            .from(challenge)
            .innerJoin(challenge.course, course)
            .leftJoin(ride).on(ride.course.id.eq(course.id)
                .and(ride.isDeleted.isFalse()))
            .leftJoin(rewardEvent).on(
                rewardEvent.challenge.id.eq(challenge.id)
                    .and(rewardEvent.status.eq(EventStatus.ACTIVE))
            )
            .where(course.isDeleted.eq(false)
                .and(challenge.status.eq(ChallengeStatus.ONGOING)));

        return PageableExecutionUtils.getPage(content, pageable, total::fetchOne);
    }

    // 챌린지 진행 중인 코스 중에서 참가자가 많은 순으로 정렬 후 반환하는 메서드
    @Override
    public List<ChallengeCourseProjection> findTop10CoursesByOngoingChallengeRides(Long userId) {

        QCourseBookmark courseBookmarkSub = new QCourseBookmark("courseBookmarkSub");
        QRide rideSub = new QRide("rideSub");

        return queryFactory
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
            .from(challenge)
            .innerJoin(challenge.course, course)
            .leftJoin(ride).on(ride.course.id.eq(course.id)
                .and(ride.isDeleted.isFalse()))
            .leftJoin(rewardEvent).on(
                rewardEvent.challenge.id.eq(challenge.id)
                    .and(rewardEvent.status.eq(EventStatus.ACTIVE)))
            .where(course.isDeleted.eq(false)
                .and(challenge.status.eq(ChallengeStatus.ONGOING)))
            .groupBy(course.id, course.name, course.level, course.distance,
                course.estimatedTime, course.sigun, course.image,
                challenge.status, challenge.endedAt,
                rewardEvent.id, rewardEvent.status, rewardEvent.type, rewardEvent.value)
            .orderBy(ride.count().coalesce(0L).desc())
            .limit(10)
            .fetch();
    }

    @Override
    public List<Challenge> findExistingChallengesByCourse(List<Course> courses) {
        List<ChallengeStatus> statuses = List.of(ChallengeStatus.UPCOMING, ChallengeStatus.ONGOING);
        return queryFactory
            .selectFrom(challenge)
            .where(challenge.course.in(courses)
                .and(challenge.status.in(statuses)))
            .fetch();
    }

    @Override
    public List<Challenge> findChallengesStartingOn(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return queryFactory
            .selectFrom(challenge)
            .where(challenge.startedAt.goe(startOfDay)
                .and(challenge.startedAt.loe(endOfDay))
                .and(challenge.status.eq(ChallengeStatus.UPCOMING)))
            .fetch();
    }

    @Override
    public List<Challenge> findChallengesEndingOn(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return queryFactory
            .selectFrom(challenge)
            .where(challenge.endedAt.goe(startOfDay)
                .and(challenge.endedAt.loe(endOfDay))
                .and(challenge.status.eq(ChallengeStatus.ONGOING)))
            .fetch();
    }
}
