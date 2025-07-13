package com.saisai.domain.reward.entity;

import com.saisai.domain.common.BaseEntity;
import com.saisai.domain.reward.dto.request.RewardEventReq;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    public static RewardEvent from(RewardEventReq rewardEventReq) {
        return new RewardEvent(
            rewardEventReq.startTime(),
            rewardEventReq.endTime(),
            rewardEventReq.value(),
            rewardEventReq.getType()
        );
    }

    private RewardEvent(LocalDateTime startTime, LocalDateTime endTime,
        Integer value, RewardEventType type) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = EventStatus.SCHEDULED;
        this.value = value;
        this.type = type;
    }
}
