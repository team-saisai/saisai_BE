package com.saisai.domain.ride.entity;

import static com.saisai.domain.common.exception.ExceptionCode.RIDE_NOT_IN_PROGRESS;
import static com.saisai.domain.common.exception.ExceptionCode.RIDE_NOT_PAUSED;

import com.saisai.domain.common.BaseEntity;
import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.course.entity.Course;
import com.saisai.domain.ride.dto.request.RideCompleteReq;
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
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "status", nullable = false, length = 15)
    @Enumerated(EnumType.STRING)
    private RideStatus status;

    @Column(name = "progress_rate", nullable = false)
    private Integer progressRate;

    @Column(name = "duration_second", nullable = false)
    private Long durationSecond;

    @Column(name = "actual_distance", nullable = false)
    private Double actualDistance;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    private Ride(User user, Course course) {
        this.status = RideStatus.IN_PROGRESS;
        this.user = user;
        this.course = course;
        this.progressRate = 0;
        this.durationSecond = 0L;
        this.actualDistance = 0D;
    }

    public static Ride start(User user, Course course) {
        return new Ride(user, course);
    }

    public void paused(int progressRate) {
        if (this.status != RideStatus.IN_PROGRESS) {
            throw new CustomException(RIDE_NOT_IN_PROGRESS);
        }

        this.status = RideStatus.PAUSED;
        this.progressRate = progressRate;
    }

    public void resume() {
        if (this.status != RideStatus.PAUSED) {
            throw new CustomException(RIDE_NOT_PAUSED);
        }

        this.status = RideStatus.IN_PROGRESS;
    }

    public void pausedForAdmin(int progressRate) {
        this.status = RideStatus.PAUSED;
        this.progressRate = progressRate;
    }

    public void complete(RideCompleteReq rideCompleteReq) {
        this.status = RideStatus.COMPLETED;
        this.progressRate = 100;
        this.durationSecond = rideCompleteReq.duration();
        this.actualDistance = rideCompleteReq.actualDistance();
        this.completedAt = LocalDateTime.now();
    }
}
