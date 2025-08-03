package com.saisai.domain.course.repository;

import com.saisai.domain.course.constant.CourseSortOption;
import com.saisai.domain.challenge.dto.projection.ChallengeCourseProjection;
import com.saisai.domain.course.dto.projection.GeneralCourseProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseBookmarkRepositoryCustom {

    Page<ChallengeCourseProjection> findChallengeBookmarkCourses(Pageable pageable, CourseSortOption sortOption, Long userId);

    Page<GeneralCourseProjection> findGeneralBookmarkCourses(Pageable pageable, CourseSortOption sortOption, Long userId);

}
