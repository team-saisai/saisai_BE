package com.saisai.domain.challenge.repository;

import com.saisai.domain.challenge.dto.projection.ChallengeCardProjection;
import java.util.List;

public interface ChallengeRepositoryCustom {

    // 챌린지 진행 중인 코스 중에서 참가자가 많은 순으로 정렬 후 반환하는 메서드
    List<ChallengeCardProjection> findTop10CoursesByOngoingChallengeRides();

}
