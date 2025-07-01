package com.saisai.domain.challenge.dto.projection;

import com.querydsl.core.annotations.QueryProjection;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import java.time.LocalDateTime;

public record ChallengeInfoProjection(
    Long challengeId,
    Long courseId,
    ChallengeStatus challengeStatus,
    LocalDateTime endedAt,
    Long participantCount
) {

    @QueryProjection
    public ChallengeInfoProjection(
        Long challengeId,
        Long courseId,
        ChallengeStatus challengeStatus,
        LocalDateTime endedAt,
        Long participantCount
    ) {
        this.challengeId = challengeId;
        this.courseId = courseId;
        this.challengeStatus = challengeStatus;
        this.endedAt = endedAt;
        this.participantCount = participantCount;
    }
}