package com.saisai.domain.course.entity;

import com.saisai.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "course_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "course_name", unique = true, nullable = false)
    private String courseName;

    @Column(name = "url", nullable = false)
    protected String url;

    @Builder
    public CourseImage (String courseName, String url) {
        this.courseName = courseName;
        this.url = url;
    }
}
