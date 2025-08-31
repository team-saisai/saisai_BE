package com.saisai.domain.badge.entity;

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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;

@Getter
@Entity
@Table(name = "user_badges", uniqueConstraints =
    @UniqueConstraint(
        name = "USER_BADGE_UNIQUE",
        columnNames = {"badge_id", "user_id"}
    )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE user_badges SET is_deleted = true, deleted_at = NOW() WHERE id = ?")
@Filter(name = "notDeleted", condition = "is_deleted = :isDeleted")
public class UserBadge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private UserBadge(Badge badge, User user) {
        this.badge = badge;
        this.user = user;
    }

    public static UserBadge create(Badge badge, User user) {
        return UserBadge.builder()
            .badge(badge)
            .user(user)
            .build();
    }

    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
