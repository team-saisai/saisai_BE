package com.saisai.domain.badge.dto.response;

import java.util.List;

public record UserBadgeRes(
    List<Long> userBadgeIds
) {

    public static UserBadgeRes of (List<Long> userBadgeIds) {
        return new UserBadgeRes(userBadgeIds);
    }
}
