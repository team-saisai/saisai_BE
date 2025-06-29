package com.saisai.domain.badge.service;

import static com.saisai.domain.common.exception.ExceptionCode.BADGE_NAME_DUPLICATE;
import static com.saisai.domain.common.exception.ExceptionCode.USER_NOT_FOUND;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.badge.dto.request.BadgeRegisterReq;
import com.saisai.domain.badge.dto.response.BadgeRegisterRes;
import com.saisai.domain.badge.dto.response.BadgeSummaryRes;
import com.saisai.domain.badge.entity.Badge;
import com.saisai.domain.badge.repository.BadgeRepository;
import com.saisai.domain.badge.repository.UserBadgeRepository;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.common.utils.ImageUtil;
import com.saisai.domain.user.entity.User;
import com.saisai.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;
    private final ImageUtil imageUtil;

    @Transactional
    public BadgeRegisterRes createBadge(BadgeRegisterReq badgeRegisterReq) {
        if(badgeRepository.existsByName(badgeRegisterReq.name())) {
            throw new CustomException(BADGE_NAME_DUPLICATE);
        }

        String image = imageUtil.upload(badgeRegisterReq.imageFile(), "badge");

        Badge badge = Badge.builder()
            .name(badgeRegisterReq.name())
            .description(badgeRegisterReq.description())
            .image(image)
            .build();

        Badge saveBadge = badgeRepository.save(badge);

        return BadgeRegisterRes.from(saveBadge);
    }

    public List<BadgeSummaryRes> getMyBadgeList(AuthUserDetails authUserDetails) {
        User user = userRepository.findById(authUserDetails.userId())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        List<BadgeSummaryRes> badges = userBadgeRepository.findBadgeByUserId(user.getId());

        return badges.stream()
            .map(badge -> new BadgeSummaryRes(
                badge.badgeId(),
                badge.badgeName(),
                imageUtil.getImageUrl(badge.badgeImageUrl())
            ))
            .toList();
    }
}
