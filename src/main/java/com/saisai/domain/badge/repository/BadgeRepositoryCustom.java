package com.saisai.domain.badge.repository;

import com.saisai.domain.badge.dto.response.BadgeDetailRes;
import java.util.List;

public interface BadgeRepositoryCustom {

    List<BadgeDetailRes> findAllBadgesWithUser(Long userId);

}
