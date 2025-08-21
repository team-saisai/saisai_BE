package com.saisai.domain.badge.repository;

import com.saisai.domain.badge.entity.UserBadge;
import com.saisai.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    List<UserBadge> findAllByUser(User user);

    @Query("SELECT ub.badge.name " +
        "FROM UserBadge ub " +
        "WHERE ub.user.id = :userId")
    List<String> findBadgeNamesByUserId(@Param("userId") Long userId);
}
