package com.saisai.domain.badge.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record BadgeSummaryRes(
    Long userBadgeId,
    String badgeName,
    String badgeImageUrl
) {

    @QueryProjection
    public BadgeSummaryRes {
    }
}
