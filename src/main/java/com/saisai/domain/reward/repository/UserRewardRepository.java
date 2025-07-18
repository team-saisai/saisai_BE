package com.saisai.domain.reward.repository;

import com.saisai.domain.reward.entity.UserReward;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRewardRepository extends JpaRepository<UserReward, Long> {

    @Query("SELECT ur "
        + "FROM UserReward ur "
        + "JOIN FETCH ur.course "
        + "WHERE ur.user.id = :userId "
        + "ORDER BY ur.createdAt DESC")
    List<UserReward> findAllByUserId(@Param("userId") Long userId);

}
