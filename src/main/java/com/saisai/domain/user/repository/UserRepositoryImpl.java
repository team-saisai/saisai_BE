package com.saisai.domain.user.repository;

import static com.saisai.domain.badge.entity.QUserBadge.userBadge;
import static com.saisai.domain.course.entity.QCourseBookmark.courseBookmark;
import static com.saisai.domain.reward.entity.QUserReward.userReward;
import static com.saisai.domain.ride.entity.QRide.ride;
import static com.saisai.domain.user.entity.QUser.user;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.saisai.domain.user.dto.response.MypageRes;
import com.saisai.domain.user.dto.response.QMypageRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public MypageRes findUserInfoById(Long userId) {

        return queryFactory
            .select(new QMypageRes(
                user.image,
                user.nickname,
                user.email,
                JPAExpressions.select(ride.count().coalesce(0L).intValue())
                    .from(ride)
                    .where(ride.user.id.eq(userId)),
                JPAExpressions.select(courseBookmark.count().coalesce(0L).intValue())
                    .from(courseBookmark)
                    .where(courseBookmark.user.id.eq(userId)),
                JPAExpressions.select(userReward.reward.sum().coalesce(0).longValue())
                    .from(userReward)
                    .where(userReward.user.id.eq(userId)),
                JPAExpressions.select(userBadge.count().coalesce(0L).intValue())
                    .from(userBadge)
                    .where(userBadge.user.id.eq(userId))
            ))
            .from(user)
            .where(user.id.eq(userId).and(user.isDeleted.eq(false)))
            .fetchOne();
    }
}
