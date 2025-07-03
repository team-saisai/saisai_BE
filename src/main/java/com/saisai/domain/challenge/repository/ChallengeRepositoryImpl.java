package com.saisai.domain.challenge.repository;

import static com.saisai.domain.challenge.entity.QChallenge.challenge;
import static com.saisai.domain.ride.entity.QRide.ride;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saisai.domain.challenge.dto.projection.ChallengeCardProjection;
import com.saisai.domain.challenge.dto.projection.QChallengeCardProjection;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.ride.entity.RideStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChallengeRepositoryImpl implements ChallengeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 챌린지 진행 중인 코스 중에서 참가자가 많은 순으로 정렬 후 반환하는 메서드
    @Override
    public List<ChallengeCardProjection> findTop10CoursesByOngoingChallengeRides() {
        return queryFactory
            .select(new QChallengeCardProjection(
                challenge.id,
                challenge.course.id,
                challenge.status,
                challenge.endedAt,
                ride.id.count().coalesce(0L)
            ))
            .from(challenge)
            .leftJoin(ride).on(
                ride.course.id.eq(challenge.course.id)
                    .and(ride.status.eq(RideStatus.IN_PROGRESS))
            )
            .where(challenge.status.eq(ChallengeStatus.ONGOING))
            .groupBy(
                challenge.id,
                challenge.course.id,
                challenge.status,
                challenge.endedAt
            )
            .orderBy(ride.count().coalesce(0L).desc())
            .limit(10)
            .fetch();
    }
}
