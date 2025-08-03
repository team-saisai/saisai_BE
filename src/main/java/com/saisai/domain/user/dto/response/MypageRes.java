package com.saisai.domain.user.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record MypageRes(
    String imageUrl,
    String nickname,
    String email,
    Integer rideCount,
    Integer bookmarkCount,
    Long reward,
    Integer badgeCount
) {
    @QueryProjection
    public MypageRes {}
}
