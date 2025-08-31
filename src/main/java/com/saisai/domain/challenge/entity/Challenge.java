package com.saisai.domain.challenge.entity;

import com.saisai.domain.common.BaseEntity;
import com.saisai.domain.course.entity.Course;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "challenge")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Challenge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "status", nullable = false, length = 15)
    @Enumerated(EnumType.STRING)
    private ChallengeStatus status;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at", nullable = false)
    private LocalDateTime endedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Challenge(Course course, LocalDateTime endedAt,
        LocalDateTime startedAt) {
        this.course = course;
        this.status = ChallengeStatus.UPCOMING;
        this.endedAt = endedAt;
        this.startedAt = startedAt;
    }

    public static Challenge create (Course course, LocalDateTime startedAt, LocalDateTime endedAt) {
        return Challenge.builder()
            .course(course)
            .startedAt(startedAt)
            .endedAt(endedAt)
            .build();
    }

    public void start () {
        this.status = ChallengeStatus.ONGOING;
        this.course.markAsIsVisible();
    }

    public void end () {
        this.status = ChallengeStatus.ENDED;
    }
}
