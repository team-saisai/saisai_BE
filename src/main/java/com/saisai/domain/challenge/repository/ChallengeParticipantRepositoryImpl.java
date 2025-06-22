package com.saisai.domain.challenge.repository;

import static com.saisai.domain.challenge.entity.QChallenge.challenge;
import static com.saisai.domain.challenge.entity.QChallengeParticipant.challengeParticipant;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saisai.domain.challenge.dto.response.ChallengeInfoProjection;
import com.saisai.domain.challenge.dto.response.QChallengeInfoProjection;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChallengeParticipantRepositoryImpl implements ChallengeParticipantRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChallengeInfoProjection> findTop10PopularChallenges() {
        return queryFactory
            .select(new QChallengeInfoProjection(
                challenge.id,
                challenge.courseName,
                challenge.status,
                challenge.endedAt,
                challengeParticipant.user.id.count().as("participantCount")
            ))
            .from(challengeParticipant)
            .join(challenge).on(challengeParticipant.challenge.id.eq(challenge.id))
            .where(challenge.status.eq(ChallengeStatus.ONGOING))
            .groupBy(
                challenge.id,
                challenge.courseName,
                challenge.status,
                challenge.endedAt
            )
            .orderBy(challengeParticipant.user.id.count().desc())
            .limit(10)
            .fetch();
    }
}
