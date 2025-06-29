package com.saisai.domain.badge.repository;

import com.saisai.domain.badge.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    boolean existsByName(String name);
}
