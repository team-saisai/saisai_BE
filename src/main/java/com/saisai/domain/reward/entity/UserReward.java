package com.saisai.domain.reward.entity;

import com.saisai.domain.challenge.entity.Challenge;
import com.saisai.domain.common.BaseEntity;
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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_rewards", uniqueConstraints = {
    @UniqueConstraint(
        name = "USER_CHALLENGE_UNIQUE",
        columnNames = {"user_id", "challenge_id"}
    )
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserReward extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;

    @Column(name = "acquired_at", nullable = false, updatable = false)
    private LocalDateTime acquiredAt;

    @Column(name = "status", nullable = false, length = 15)
    @Enumerated(EnumType.STRING)
    private RewardDeliveryStatus status;

    public UserReward(Reward reward, User user) {
        this.status = RewardDeliveryStatus.PENDING_REQUEST;
        this.acquiredAt = LocalDateTime.now();
        this.reward = reward;
        this.user = user;
    }
}