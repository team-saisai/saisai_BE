package com.saisai.domain.course.repository;

import com.saisai.domain.course.entity.CourseSave;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseSaveRepository extends JpaRepository<CourseSave, Long> {

    boolean existsByCourseIdAndUserId(Long courseId, Long userId);
}
