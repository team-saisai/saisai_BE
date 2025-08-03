package com.saisai.domain.challenge.repository;

import com.saisai.domain.course.constant.CourseSortOption;
import com.saisai.domain.challenge.dto.projection.ChallengeCourseProjection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChallengeRepositoryCustom {

    Page<ChallengeCourseProjection> findChallengeCourses(Pageable pageable,
        CourseSortOption sortOption, Long userId);

    // 챌린지 진행 중인 코스 중에서 참가자가 많은 순으로 정렬 후 반환하는 메서드
    List<ChallengeCourseProjection> findTop10CoursesByOngoingChallengeRides(Long userId);

}
