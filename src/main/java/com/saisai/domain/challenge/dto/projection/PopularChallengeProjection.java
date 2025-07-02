package com.saisai.domain.challenge.dto.projection;

import com.querydsl.core.annotations.QueryProjection;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import java.time.LocalDateTime;

public record PopularChallengeProjection(
    Long challengeId,
    Long courseId,
    ChallengeStatus challengeStatus,
    LocalDateTime endedAt,
    Long challengerCount
) {

    @QueryProjection
    public PopularChallengeProjection { }
}