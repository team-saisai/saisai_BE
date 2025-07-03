package com.saisai.domain.challenge.service;

import com.saisai.domain.challenge.dto.projection.PopularChallengeProjection;
import com.saisai.domain.challenge.dto.response.PopularChallengeRes;
import com.saisai.domain.challenge.repository.ChallengeRepository;
import com.saisai.domain.common.utils.s3.ImageUtil;
import com.saisai.domain.course.dto.projection.CourseCardProjection;
import com.saisai.domain.course.repository.CourseRepository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final CourseRepository courseRepository;
    private final ImageUtil imageUtil;

    // 현재 인기 챌린지 조회 메서드
    public List<PopularChallengeRes> getPopularChallenges() {
        // 인기 코스Id + 도전자 수 조회
        List<PopularChallengeProjection> popularChallengeInfos = challengeRepository.findTop10CoursesByOngoingChallengeRides();

        if (popularChallengeInfos.isEmpty()) {
            return Collections.emptyList();
        }

        // 인기 코스 ID 매핑
        List<Long> courseIds = popularChallengeInfos.stream()
            .map(PopularChallengeProjection::courseId)
            .toList();

        // 코스 정보 조회
        Map<Long, CourseCardProjection> courseCardMap = courseRepository
            .findCourseCardByIds(courseIds).stream()
            .collect(Collectors.toMap(
                CourseCardProjection::courseId,
                Function.identity()
            ));

        return popularChallengeInfos.stream()
            .map(popularChallengesInfo -> {
                CourseCardProjection courseInfo = courseCardMap.get(popularChallengesInfo.courseId());
                String courseImageUrl = imageUtil.getImageUrl(courseInfo.image());

                return PopularChallengeRes.from(popularChallengesInfo, courseInfo, courseImageUrl);
            })
            .toList();
    }
}
