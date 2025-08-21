package com.saisai.domain.challenge.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record CreateChallengeRes(
    List<Long> createdChallengeCourseIds,
    LocalDateTime startedAt,
    LocalDateTime closedAt,
    Set<Long> alreadyExistingChallengeCourseIds
) {
    public static CreateChallengeRes of (List<Long> courseIds, LocalDateTime startedAt, LocalDateTime closedAt, Set<Long> existingIds) {
        return new CreateChallengeRes(
            courseIds,
            startedAt,
            closedAt,
            existingIds
        );
    }
}
