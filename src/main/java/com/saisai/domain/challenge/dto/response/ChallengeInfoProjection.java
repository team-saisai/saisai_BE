package com.saisai.domain.challenge.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.saisai.domain.challenge.entity.ChallengeStatus;
import java.time.LocalDateTime;

public record ChallengeInfoProjection(
    Long challengeId,
    String courseName,
    ChallengeStatus challengeStatus,
    LocalDateTime endedAt,
    Long participantCount
) {

    @QueryProjection
    public ChallengeInfoProjection(
        Long challengeId,
        String courseName,
        ChallengeStatus challengeStatus,
        LocalDateTime endedAt,
        Long participantCount
    ) {
        this.challengeId = challengeId;
        this.courseName = courseName;
        this.challengeStatus = challengeStatus;
        this.endedAt = endedAt;
        this.participantCount = participantCount;
    }
}
