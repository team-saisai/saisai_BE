package com.saisai.domain.course.repository;

import com.saisai.domain.course.entity.CourseImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseImageRepository extends JpaRepository<CourseImage, Long> {
    CourseImage findCourseImageByCourseName (String courseName);
}
