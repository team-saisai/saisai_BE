package com.saisai.domain.challenge.repository;

import static com.saisai.domain.challenge.entity.QChallenge.challenge;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChallengeRepositoryImpl implements ChallengeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findChallengeByStatus(ChallengeStatus status) {
        return queryFactory.select(challenge.courseName)
            .from(challenge)
            .where(challenge.status.eq(ChallengeStatus.ONGOING))
            .fetch();
    }
}
