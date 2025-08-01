package com.saisai.domain.challenge.service;

import com.saisai.domain.challenge.dto.projection.ChallengeCardProjection;
import com.saisai.domain.challenge.repository.ChallengeRepository;
import com.saisai.domain.common.aws.s3.ImageUtil;
import com.saisai.domain.course.dto.projection.CourseCardProjection;
import com.saisai.domain.course.dto.response.CourseCardRes;
import com.saisai.domain.course.repository.CourseRepository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
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
    public List<CourseCardRes> getPopularChallenges() {
        // 인기 코스Id + 도전자 수 조회
        List<ChallengeCardProjection> popularChallengeInfos = challengeRepository.findTop10CoursesByOngoingChallengeRides();

        if (popularChallengeInfos.isEmpty()) {
            return Collections.emptyList();
        }

        return popularChallengeInfos.stream()
            .map(popularChallengesInfo -> {
                CourseCardProjection courseInfo = courseCardMap.get(popularChallengesInfo.courseId());
                String courseImageUrl = imageUtil.getImageUrl(courseInfo.image());

                return CourseCardRes.from(popularChallengesInfo, courseInfo, courseImageUrl);
            })
            .toList();
    }
}
