package com.saisai.domain.badge.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record BadgeSummaryRes(
    Long badgeId,
    String badgeName,
    String badgeImageUrl
) {

    @QueryProjection
    public BadgeSummaryRes {
    }
}
