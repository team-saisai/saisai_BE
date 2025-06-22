package com.saisai.domain.challenge.entity;

import com.saisai.domain.common.BaseEntity;
import com.saisai.domain.reward.entity.Reward;
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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "challenge", uniqueConstraints = {
    @UniqueConstraint(
        name = "COURSE_REWARD_UNIQUE",
        columnNames = {"course_name","reward_id"}
    )
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Challenge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "course_name", unique = true, nullable = false)
    private String courseName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;


    @Column(name = "status", nullable = false, length = 15)
    @Enumerated(EnumType.STRING)
    private ChallengeStatus status;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "closed_at", nullable = false)
    private LocalDateTime endedAt;

    @Builder
    public Challenge(String courseName, Reward reward, ChallengeStatus status, LocalDateTime endedAt,
        LocalDateTime startedAt) {
        this.courseName = courseName;
        this.reward = reward;
        this.status = status;
        this.endedAt = endedAt;
        this.startedAt = startedAt;
    }
}
