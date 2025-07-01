package com.saisai.domain.course.repository;

import com.saisai.domain.course.dto.projection.CoursePageProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseRepositoryCustom {
    Page<CoursePageProjection> findCoursesByChallengeStatus(String challengeStatus, Pageable pageable);
}
