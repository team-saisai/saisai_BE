package com.saisai.domain.badge.repository;

import com.saisai.domain.badge.dto.response.BadgeSummaryRes;
import java.util.List;

public interface UserBadgeRepositoryCustom {

    List<BadgeSummaryRes> findBadgeByUserId (Long userId);
}
