package com.saisai.domain.badge.dto.response;

import com.saisai.domain.badge.entity.Badge;
import com.saisai.domain.badge.entity.UserBadge;
import java.time.LocalDate;

public record BadgeDetailRes(
    String badgeName,
    String badgeDescription,
    String badgeImage,
    LocalDate acquiredAt
) {

    public static BadgeDetailRes from(Badge badge, UserBadge userBadge, String badgeImage) {
        return new BadgeDetailRes(
            badge.getName(),
            badge.getDescription(),
            badgeImage,
            userBadge.getCreatedAt().toLocalDate()
        );
    }
}
