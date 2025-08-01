package com.saisai.domain.challenge.service;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.challenge.dto.response.PopularChallengeCourseRes;
import com.saisai.domain.challenge.repository.ChallengeRepository;
import com.saisai.domain.common.aws.s3.ImageUtil;
import com.saisai.domain.course.dto.projection.ChallengeCourseProjection;
import com.saisai.domain.reward.dto.projection.RewardEventProjection;
import com.saisai.domain.reward.util.RewardUtils;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ImageUtil imageUtil;

    // 현재 인기 챌린지 조회 메서드
    public List<PopularChallengeCourseRes> getPopularChallenges(AuthUserDetails authUserDetails) {
        // 인기 코스Id + 도전자 수 조회
        List<ChallengeCourseProjection> popularChallengeInfos = challengeRepository.findTop10CoursesByOngoingChallengeRides(authUserDetails.userId());

        if (popularChallengeInfos.isEmpty()) {
            return Collections.emptyList();
        }

        return popularChallengeInfos.stream()
            .map(popularChallengeInfo -> {
                String imageUrl = imageUtil.getImageUrl(popularChallengeInfo.imageUrl());
                boolean isEventActive = isRewardEventActive(popularChallengeInfo.rewardEventProjection());
                int reward = calculateReward(popularChallengeInfo, isEventActive);

                return PopularChallengeCourseRes.from(popularChallengeInfo, imageUrl, isEventActive, reward);
            })
            .toList();
    }

    // 이벤트 활성화 확인
    private boolean isRewardEventActive(RewardEventProjection rewardEventProjection) {
        return Optional.ofNullable(rewardEventProjection)
            .map(RewardEventProjection::rewardEventId)
            .isPresent();
    }

    // 리워드 계산
    private int calculateReward(ChallengeCourseProjection challengeCourseProjection, boolean isEventActive) {
        return isEventActive ?
            RewardUtils.calculateEventReward(
                challengeCourseProjection.level(),
                challengeCourseProjection.rewardEventProjection().rewardEventType(),
                challengeCourseProjection.rewardEventProjection().value()) :
            RewardUtils.calculateEventReward(challengeCourseProjection.level());
    }


}
