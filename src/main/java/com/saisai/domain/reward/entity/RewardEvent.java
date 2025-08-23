package com.saisai.domain.reward.entity;

import com.saisai.domain.challenge.entity.Challenge;
import com.saisai.domain.common.BaseEntity;
import com.saisai.domain.reward.dto.request.RewardEventReq;
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
@Table(name = "reward_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RewardEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventStatus status;

    @Column(name = "value", nullable = false)
    private Integer value;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private RewardEventType type;

    public static RewardEvent from(RewardEventReq rewardEventReq, Challenge challenge) {
        return new RewardEvent(
            rewardEventReq.startTime(),
            rewardEventReq.endTime(),
            rewardEventReq.value(),
            rewardEventReq.getType(),
            challenge
        );
    }

    private RewardEvent(LocalDateTime startTime, LocalDateTime endTime,
        Integer value, RewardEventType type, Challenge challenge) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = EventStatus.SCHEDULED;
        this.value = value;
        this.type = type;
        this.challenge = challenge;
    }

    public void start() {
        this.status = EventStatus.ACTIVE;
    }

    public void end() {
        this.status = EventStatus.EXPIRED;
    }
}
