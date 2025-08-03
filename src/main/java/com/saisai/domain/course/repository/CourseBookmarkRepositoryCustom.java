package com.saisai.domain.course.repository;

import com.saisai.domain.course.dto.projection.CourseUnifiedProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseBookmarkRepositoryCustom {

    Page<CourseUnifiedProjection>  findByBookmarkCourses(Pageable pageable, Long userId);

}
