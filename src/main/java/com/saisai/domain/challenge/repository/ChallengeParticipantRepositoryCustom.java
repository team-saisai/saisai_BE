package com.saisai.domain.challenge.repository;

import com.saisai.domain.challenge.dto.response.ChallengeInfoProjection;
import java.util.List;

public interface ChallengeParticipantRepositoryCustom {
    List<ChallengeInfoProjection> findTop10PopularChallenges();
}
