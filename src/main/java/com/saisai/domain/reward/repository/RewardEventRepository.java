package com.saisai.domain.reward.repository;

import com.saisai.domain.reward.entity.RewardEvent;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RewardEventRepository extends JpaRepository<RewardEvent, Long> {

    // 이벤트 겹치는 코스ID 조회
    @Query("SELECT ec.course.id FROM EventCourse ec " +
        "WHERE ec.course.id IN :courseIds " +
        "AND ec.rewardEvent.status IN ('SCHEDULED', 'ACTIVE') " +
        "AND ((ec.rewardEvent.startTime <= :endTime AND ec.rewardEvent.endTime >= :startTime))")
    List<Long> findConflictingCourseIds(@Param("courseIds") List<Long> courseIds,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);


}
