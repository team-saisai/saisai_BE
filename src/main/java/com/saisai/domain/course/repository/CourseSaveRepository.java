package com.saisai.domain.course.repository;

import com.saisai.domain.course.entity.CourseLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseSaveRepository extends JpaRepository<CourseLike, Long> {

}
