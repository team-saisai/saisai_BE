package com.saisai.domain.user.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.saisai.domain.auth.constant.ProviderType;

public record MypageRes(
    String imageUrl,
    String nickname,
    String email,
    Integer rideCount,
    Integer bookmarkCount,
    Long reward,
    Integer badgeCount,
    ProviderType provider
) {
    @QueryProjection
    public MypageRes {}
}
