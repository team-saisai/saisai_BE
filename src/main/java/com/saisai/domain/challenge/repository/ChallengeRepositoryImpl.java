package com.saisai.domain.challenge.repository;

import static com.saisai.domain.challenge.entity.QChallenge.challenge;
import static com.saisai.domain.course.entity.QCourse.course;
import static com.saisai.domain.reward.entity.QRewardEvent.rewardEvent;
import static com.saisai.domain.ride.entity.QRide.ride;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.course.dto.projection.ChallengeCourseProjection;
import com.saisai.domain.course.dto.projection.QChallengeCourseProjection;
import com.saisai.domain.reward.dto.projection.QRewardEventProjection;
import com.saisai.domain.reward.entity.EventStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChallengeRepositoryImpl implements ChallengeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 챌린지 진행 중인 코스 중에서 참가자가 많은 순으로 정렬 후 반환하는 메서드
    @Override
    public List<ChallengeCourseProjection> findTop10CoursesByOngoingChallengeRides(Long userId) {
        return queryFactory
            .select(new QChallengeCardProjection(
                challenge.id,
                challenge.course.id,
            .select(new QChallengeCourseProjection(
                course.id,
                course.name,
                course.level,
                course.distance,
                course.estimatedTime,
                course.sigun,
                course.image,
                ride.count().coalesce(0L),
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
            .leftJoin(ride).on(ride.course.id.eq(course.id))
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
}
