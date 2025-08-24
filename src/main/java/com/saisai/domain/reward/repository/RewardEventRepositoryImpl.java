package com.saisai.domain.reward.repository;

import static com.saisai.domain.challenge.entity.QChallenge.challenge;
import static com.saisai.domain.course.entity.QCourse.course;
import static com.saisai.domain.reward.entity.QRewardEvent.rewardEvent;
import static com.saisai.domain.ride.entity.QRide.ride;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.reward.dto.projection.QRewardInfo;
import com.saisai.domain.reward.dto.projection.RewardInfo;
import com.saisai.domain.reward.entity.EventStatus;
import com.saisai.domain.reward.entity.RewardEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RewardEventRepositoryImpl implements RewardEventCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<RewardEvent> findRewardEventScheduled(LocalDate date) {

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return queryFactory
            .selectFrom(rewardEvent)
            .where(rewardEvent.startTime.goe(startOfDay)
                .and(rewardEvent.startTime.loe(endOfDay))
                .and(rewardEvent.status.eq(EventStatus.SCHEDULED)))
            .fetch();
    }

    @Override
    public List<RewardEvent> findRewardEventActived(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return queryFactory
            .selectFrom(rewardEvent)
            .where(rewardEvent.startTime.goe(startOfDay)
                .and(rewardEvent.startTime.loe(endOfDay))
                .and(rewardEvent.status.eq(EventStatus.ACTIVE)))
            .fetch();
    }

    @Override
    public Optional<RewardInfo> findRewardInfoByRideId(Long rideId) {
        return Optional.ofNullable(queryFactory
            .select(new QRewardInfo(
                course,
                rewardEvent.type,
                rewardEvent.value
            ))
            .from(ride)
            .join(ride.course, course)
            .join(challenge).on(challenge.course.eq(course).and(challenge.status.eq(ChallengeStatus.ONGOING)))
            .leftJoin(rewardEvent).on(rewardEvent.challenge.eq(challenge).and(rewardEvent.status.eq(
                EventStatus.ACTIVE)))
            .where(ride.id.eq(rideId))
            .fetchOne());
    }
}
