package com.saisai.domain.mission.service;

import static com.saisai.domain.badge.constant.BadgeConstants.CONSECUTIVE_RIDING_3_DAYS;
import static com.saisai.domain.badge.constant.BadgeConstants.FIRST_COURSE_COMPLETE;
import static com.saisai.domain.badge.constant.BadgeConstants.HARD_COURSE_10_COMPLETE;
import static com.saisai.domain.badge.constant.BadgeConstants.TOTAL_COURSE_30_COMPLETE;
import static com.saisai.domain.badge.constant.BadgeConstants.UNIQUE_REGION_5_COMPLETE;
import static com.saisai.domain.common.exception.ExceptionCode.BADGE_NOT_FOUND;

import com.saisai.domain.badge.constant.BadgeConstants;
import com.saisai.domain.badge.entity.Badge;
import com.saisai.domain.badge.entity.UserBadge;
import com.saisai.domain.badge.repository.BadgeRepository;
import com.saisai.domain.badge.repository.UserBadgeRepository;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.ride.repository.RideRepository;
import com.saisai.domain.user.entity.User;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MissionService {

    private final RideRepository rideRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void checkAndGrantAllMissions(User user) {

        List<String> heldBadgeNamesList = userBadgeRepository.findBadgeNamesByUserId(user.getId());
        Set<String> heldBadgeNamesSet = new HashSet<>(heldBadgeNamesList);
        if (heldBadgeNamesSet.size() == BadgeConstants.ALL_MISSION_BADGE_NAMES.size()) {
            log.info("유저 {}는 이미 모든 뱃지를 보유하고 있습니다. 미션 검사를 건너뜁니다.", user.getId());
            return;
        }

        long completedRidesCount = rideRepository.countCompletedRides(user.getId());
        long uniqueRegionsCount = rideRepository.countDistinctSigunsByUserId(user.getId());
        long hardCourseCount = rideRepository.countCompletedHardCoursesByUserId(user.getId());

        processMissions(user, heldBadgeNamesSet, completedRidesCount, uniqueRegionsCount, hardCourseCount);
    }

    private void processMissions(User user, Set<String> userBadges, long completedRidesCount, long uniqueRegionsCount, long hardCourseCount) {
        // 이미 가지고 있는 뱃지를 제외한 나머지 뱃지 목록을 생성
        Set<String> missionsToCheck = new HashSet<>(BadgeConstants.ALL_MISSION_BADGE_NAMES);
        missionsToCheck.removeAll(userBadges);

        for (String badgeName : missionsToCheck) {
            switch (badgeName) {
                case FIRST_COURSE_COMPLETE:
                    if (completedRidesCount == 1) grantBadge(user, badgeName);
                    break;
                case TOTAL_COURSE_30_COMPLETE:
                    if (completedRidesCount >= 30) grantBadge(user, badgeName);
                    break;
                case UNIQUE_REGION_5_COMPLETE:
                    if (uniqueRegionsCount >= 5) grantBadge(user, badgeName);
                    break;
                case CONSECUTIVE_RIDING_3_DAYS:
                    if (user.getConsecutiveDays() != null && user.getConsecutiveDays() >= 3) grantBadge(user, badgeName);
                    break;
                case HARD_COURSE_10_COMPLETE:
                    if (hardCourseCount >= 10) grantBadge(user, badgeName);
                    break;
                default:
                    throw new CustomException(BADGE_NOT_FOUND, "name: " + badgeName);
            }
        }
    }

    private void grantBadge(User user, String badgeName) {
        Badge badge = badgeRepository.findByName(badgeName)
            .orElseThrow(() -> new CustomException(BADGE_NOT_FOUND, "name: " + badgeName));
        UserBadge userBadge = UserBadge.create(badge, user);
        userBadgeRepository.save(userBadge);

        log.info("유저 {}가 뱃지 '{}'를 획득했습니다.", user.getId(), badgeName);
    }

}
