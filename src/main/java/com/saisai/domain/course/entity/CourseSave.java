package com.saisai.domain.course.entity;

import com.saisai.domain.common.BaseEntity;
import com.saisai.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "course_saves", uniqueConstraints = {
    @UniqueConstraint(
        name = "USER_COURSE_UNIQUE",
        columnNames = {"user_id", "course_id"}
    )
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseSave extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;    // DDL으로 ON DELETE CASCADE 적용?

    public static CourseSave from(User user,  Course course) {
        return new CourseSave(
            user,
            course
        );
    }

    private CourseSave(User user,  Course course) {
        this.user = user;
        this.course = course;
    }
}
