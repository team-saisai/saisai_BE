package com.saisai.domain.challenge.repository;

import com.saisai.domain.challenge.entity.ChallengeStatus;
import java.util.List;

public interface ChallengeRepositoryCustom {
    List<String> findChallengeByStatus(ChallengeStatus status);
}
