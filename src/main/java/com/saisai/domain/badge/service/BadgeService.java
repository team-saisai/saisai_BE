package com.saisai.domain.badge.service;

import static com.saisai.domain.common.exception.ExceptionCode.BADGE_NAME_DUPLICATE;

import com.saisai.config.jwt.AuthUserDetails;
import com.saisai.domain.badge.dto.request.BadgeRegisterReq;
import com.saisai.domain.badge.dto.response.BadgeDetailRes;
import com.saisai.domain.badge.dto.response.BadgeRegisterRes;
import com.saisai.domain.badge.entity.Badge;
import com.saisai.domain.badge.repository.BadgeRepository;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.infra.aws.s3.ImageUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final ImageUtil imageUtil;

    @Transactional
    public BadgeRegisterRes createBadge(BadgeRegisterReq badgeRegisterReq) {
        if(badgeRepository.existsByName(badgeRegisterReq.name())) {
            throw new CustomException(BADGE_NAME_DUPLICATE);
        }

        String colorImage = imageUtil.upload(badgeRegisterReq.colorImageFile(), "badge");
        String blackImage = imageUtil.upload(badgeRegisterReq.blackImageFile(), "badge");

        Badge badge = Badge.builder()
            .name(badgeRegisterReq.name())
            .description(badgeRegisterReq.description())
            .colorImage(colorImage)
            .blackImage(blackImage)
            .condition(badgeRegisterReq.condition())
            .build();

        Badge saveBadge = badgeRepository.save(badge);

        return BadgeRegisterRes.from(saveBadge);
    }

    public List<BadgeDetailRes> getMyBadgeList(AuthUserDetails authUserDetails) {

        List<BadgeDetailRes> badgeDetails = badgeRepository.findAllBadgesWithUser(authUserDetails.userId());

        return badgeDetails.stream()
            .map(dto -> {
                String imageUrl = imageUtil.getImageUrl(dto.image());
                return new BadgeDetailRes(
                    dto.id(),
                    dto.name(),
                    imageUrl,
                    dto.description(),
                    dto.condition()
                );
            })
            .toList();
    }

}
