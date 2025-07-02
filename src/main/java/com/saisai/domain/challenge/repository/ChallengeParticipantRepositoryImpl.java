package com.saisai.domain.challenge.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChallengeParticipantRepositoryImpl implements ChallengeParticipantRepositoryCustom {

    private final JPAQueryFactory queryFactory;
}
