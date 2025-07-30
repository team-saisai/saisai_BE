package com.saisai.domain.reward.repository;

import com.saisai.domain.reward.entity.RewardEvent;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RewardEventRepository extends JpaRepository<RewardEvent, Long> {

    // 이벤트 겹치는 코스ID 조회
    @Query("SELECT re.challenge.id FROM RewardEvent re " +
        "WHERE re.challenge.id IN :challengeIds " +
        "AND re..status IN ('SCHEDULED', 'ACTIVE') " +
        "AND ((re.startTime <= :endTime AND re.endTime >= :startTime))")
    List<Long> findConflictingChallengeIds(@Param("challengeIds") List<Long> challengeIds,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);


}
