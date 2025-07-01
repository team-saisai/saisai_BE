package com.saisai.domain.challenge.repository;

import static com.saisai.domain.challenge.entity.QChallenge.challenge;
import static com.saisai.domain.challenge.entity.QChallengeParticipant.challengeParticipant;
import static com.saisai.domain.course.entity.QCourse.course;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saisai.domain.challenge.dto.projection.ChallengeInfoProjection;
import com.saisai.domain.challenge.dto.projection.QChallengeInfoProjection;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import com.saisai.domain.course.repository.CourseRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChallengeParticipantRepositoryImpl implements ChallengeParticipantRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final CourseRepository courseRepository;

    @Override
    public List<ChallengeInfoProjection> findTop10PopularChallenges() {
        return queryFactory
            .select(new QChallengeInfoProjection(
                challenge.id,
                course.id,
                challenge.status,
                challenge.endedAt,
                challengeParticipant.count().as("participantCount")
            ))
            .from(challengeParticipant)
            .join(challengeParticipant.challenge, challenge)
            .where(challenge.status.eq(ChallengeStatus.ONGOING))
            .groupBy(challenge.id)
            .orderBy(challengeParticipant.count().desc())
            .limit(10)
            .fetch();
    }
}
