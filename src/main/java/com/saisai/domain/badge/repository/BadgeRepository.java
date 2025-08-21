package com.saisai.domain.badge.repository;

import com.saisai.domain.badge.entity.Badge;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long>, BadgeRepositoryCustom {

    boolean existsByName(String name);

    Optional<Badge> findByName(String badgeName);
}
