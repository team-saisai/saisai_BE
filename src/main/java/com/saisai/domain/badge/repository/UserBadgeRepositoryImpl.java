package com.saisai.domain.badge.repository;

import static com.saisai.domain.badge.entity.QBadge.badge;
import static com.saisai.domain.badge.entity.QUserBadge.userBadge;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserBadgeRepositoryImpl implements UserBadgeRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Long> findBadgeByUserId(Long userId) {
        return jpaQueryFactory
            .select(userBadge.badge.id)
            .from(userBadge)
            .innerJoin(userBadge.badge, badge)
            .where(userBadge.user.id.eq(userId))
            .orderBy(userBadge.badge.id.asc())
            .fetch();
    }
}
