package com.saisai.domain.badge.repository;

import static com.saisai.domain.badge.entity.QBadge.badge;
import static com.saisai.domain.badge.entity.QUserBadge.userBadge;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saisai.domain.badge.dto.response.BadgeSummaryRes;
import com.saisai.domain.badge.dto.response.QBadgeSummaryRes;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserBadgeRepositoryImpl implements UserBadgeRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<BadgeSummaryRes> findBadgeByUserId(Long userId) {
        return jpaQueryFactory
            .select(new QBadgeSummaryRes(
                        userBadge.id,
                        badge.name,
                        badge.image))
            .from(userBadge)
            .join(userBadge.badge, badge)
            .where(userBadge.user.id.eq(userId))
            .orderBy(userBadge.createdAt.desc())
            .limit(10)
            .fetch();
    }
}
