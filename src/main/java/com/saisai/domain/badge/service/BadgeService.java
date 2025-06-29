package com.saisai.domain.badge.service;

import static com.saisai.domain.common.exception.ExceptionCode.BADGE_NAME_DUPLICATE;

import com.saisai.domain.badge.dto.request.BadgeRegisterReq;
import com.saisai.domain.badge.dto.response.BadgeRegisterRes;
import com.saisai.domain.badge.entity.Badge;
import com.saisai.domain.badge.repository.BadgeRepository;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.common.utils.ImageUtil;
import com.saisai.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BadgeService {

    private final BadgeRepository badgeRepository;
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
}
