package com.saisai.domain.course.repository;

import com.saisai.domain.course.constant.CourseSortOption;
import com.saisai.domain.course.dto.projection.CourseCardProjection;
import com.saisai.domain.course.dto.projection.CourseDetailsProjection;
import com.saisai.domain.course.dto.projection.ChallengeCourseProjection;
import com.saisai.domain.course.dto.projection.GeneralCourseProjection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseRepositoryCustom {
    List<CourseCardProjection> findCourseCardByIds(List<Long> courseIds);

    Optional<CourseDetailsProjection> findCourseDetailsProjection(Long courseId);

    Page<ChallengeCourseProjection> findChallengeCourses(Pageable pageable, CourseSortOption sortOption, Long userId);

    Page<GeneralCourseProjection> findGeneralCourses(Pageable pageable, CourseSortOption sortOption, Long userId);
}
