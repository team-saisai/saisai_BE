package com.saisai.domain.badge.dto.response;

import com.saisai.domain.badge.entity.Badge;

public record BadgeRegisterRes(
    Long badgeId,
    String badgeName
) {
    public static BadgeRegisterRes from (Badge badge) {
        return new BadgeRegisterRes(
            badge.getId(),
            badge.getName()
        );
    }
}
