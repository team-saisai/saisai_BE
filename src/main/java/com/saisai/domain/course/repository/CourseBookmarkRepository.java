package com.saisai.domain.course.repository;

import com.saisai.domain.course.entity.CourseBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseBookmarkRepository extends JpaRepository<CourseBookmark, Long> {

    boolean existsByCourseIdAndUserId(Long courseId, Long userId);
}
