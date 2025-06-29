package com.saisai.domain.badge.repository;

import com.saisai.domain.badge.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long>, UserBadgeRepositoryCustom{

}
