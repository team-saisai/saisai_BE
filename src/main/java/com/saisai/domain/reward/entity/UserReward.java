package com.saisai.domain.reward.entity;

import com.saisai.domain.common.BaseEntity;
import com.saisai.domain.course.entity.Course;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "reward", nullable = false)
    private Integer reward;

    public UserReward(User user, Course course, int reward) {
        this.user = user;
        this.course = course;
        this.reward = reward;
    }
}