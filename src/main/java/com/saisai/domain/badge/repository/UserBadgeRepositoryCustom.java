package com.saisai.domain.badge.repository;

import java.util.List;

public interface UserBadgeRepositoryCustom {

    List<Long> findBadgeByUserId (Long userId);
}
