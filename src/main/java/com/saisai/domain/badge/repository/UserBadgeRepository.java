package com.saisai.domain.badge.repository;

import com.saisai.domain.badge.entity.UserBadge;
import com.saisai.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long>, UserBadgeRepositoryCustom{

    Optional<UserBadge> findByUserAndId(User user, Long id);
}
