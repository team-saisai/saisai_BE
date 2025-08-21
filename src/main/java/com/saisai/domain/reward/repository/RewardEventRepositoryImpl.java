package com.saisai.domain.reward.repository;

import static com.saisai.domain.reward.entity.QRewardEvent.rewardEvent;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saisai.domain.reward.entity.EventStatus;
import com.saisai.domain.reward.entity.RewardEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
}
