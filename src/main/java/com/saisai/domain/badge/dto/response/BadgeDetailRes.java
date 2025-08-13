package com.saisai.domain.badge.dto.response;

import com.saisai.domain.badge.entity.Badge;

public record BadgeDetailRes(
    Long id,
    String name,
    String image,
    String description,
    String condition
) {

    public static BadgeDetailRes from(Badge badge, String badgeImage) {
        return new BadgeDetailRes(
            badge.getId(),
            badge.getName(),
            badgeImage,
            badge.getDescription(),
            badge.getCondition()
        );
    }
}
