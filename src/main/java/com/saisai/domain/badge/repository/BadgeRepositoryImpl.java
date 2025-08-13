package com.saisai.domain.badge.repository;

import static com.saisai.domain.badge.entity.QBadge.badge;
import static com.saisai.domain.badge.entity.QUserBadge.userBadge;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saisai.domain.badge.dto.response.BadgeDetailRes;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BadgeRepositoryImpl implements BadgeRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<BadgeDetailRes> findAllBadgesWithUser(Long userId) {
        return queryFactory
            .select(Projections.constructor(BadgeDetailRes.class,
                badge.id,
                badge.name,
                Expressions.cases()
                    .when(userBadge.isNotNull())
                    .then(badge.colorImage)
                    .otherwise(badge.blackImage)
                    .as("image"),
                badge.description,
                badge.condition
            ))
            .from(badge)
            .leftJoin(userBadge)
            .on(userBadge.badge.eq(badge).and(userBadge.user.id.eq(userId)))
            .fetch();
    }
}
