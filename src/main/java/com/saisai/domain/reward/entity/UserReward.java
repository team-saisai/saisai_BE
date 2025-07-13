package com.saisai.domain.reward.entity;

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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_rewards")
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

    @Column(name = "acquired_at", nullable = false, updatable = false)
    private LocalDateTime acquiredAt;

    @Column(name = "reward", nullable = false)
    private Integer reward;

    public UserReward(User user, int reward) {
        this.acquiredAt = LocalDateTime.now();
        this.user = user;
        this.reward = reward;
    }
}