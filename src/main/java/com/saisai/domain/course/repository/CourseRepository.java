package com.saisai.domain.course.repository;

import com.saisai.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long>, CourseRepositoryCustom {

    Boolean existsByDurunubiCourseId(String durunubiCourseId);
}
