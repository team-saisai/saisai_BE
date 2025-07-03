package com.saisai.domain.course.repository;

import com.saisai.domain.course.dto.projection.CourseCardProjection;
import com.saisai.domain.course.dto.projection.CoursePageProjection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseRepositoryCustom {
    Page<CoursePageProjection> findCoursesByChallengeStatus(String challengeStatus, Pageable pageable);

    List<CourseCardProjection> findCourseCardByIds(List<Long> courseIds);
}
