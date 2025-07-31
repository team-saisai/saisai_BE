package com.saisai.domain.course.repository;

import com.saisai.domain.course.entity.CourseBookmark;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseBookmarkRepository extends JpaRepository<CourseBookmark, Long> {

    boolean existsByCourseIdAndUserId(Long courseId, Long userId);

    Optional<CourseBookmark> findByCourseIdAndUserId(Long courseId, Long userId);

    int deleteByUserIdAndCourseIdIn(Long userId, Set<Long> courseIds);
}
