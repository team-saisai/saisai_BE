package com.saisai.domain.ride.entity;

import com.saisai.domain.common.BaseEntity;
import com.saisai.domain.course.entity.Course;
import com.saisai.domain.user.entity.User;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rides")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ride extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_name", nullable = false)
    private Course course;

    @Column(name = "status", nullable = false, length = 15)
    @Enumerated(EnumType.STRING)
    private RideStatus status;

    @Column(name = "progress_rate", nullable = false)
    private Double progressRate;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "paused_at")
    private LocalDateTime pausedAt;

    @Column(name = "resume_at")
    private LocalDateTime resumeAt;

    @Column(name = "certified_image", length = 255)
    private String certifiedImage;
}
