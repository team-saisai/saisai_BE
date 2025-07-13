package com.saisai.domain.reward.entity;

import com.saisai.domain.common.BaseEntity;
import com.saisai.domain.course.entity.Course;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_courses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventCourse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_event_id", nullable = false)
    private RewardEvent rewardEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private EventCourse(RewardEvent rewardEvent, Course coures) {
        this.rewardEvent = rewardEvent;
        this.course = coures;
    }

    public static EventCourse from (RewardEvent rewardEvent, Course coures) {
        return new EventCourse(rewardEvent, coures);
    }

}
