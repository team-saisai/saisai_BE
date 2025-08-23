package com.saisai.domain.user.entity;

import com.saisai.domain.auth.constant.ProviderType;
import com.saisai.domain.auth.oauth.UserInfo;
import com.saisai.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"provider_id", "provider", "deleted_at"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE users SET is_deleted = true, deleted_at = NOW() WHERE id = ?")
@Filter(name = "notDeleted", condition = "is_deleted = :isDeleted")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "image")
    private String image;

    @Column(name = "email", unique = true, length = 50)
    private String email;

    @Column(name = "password", length = 64)
    private String password;

    @Column(name = "name", nullable = false, length = 50)
    private String nickname;

    @Column(name = "role", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "provider", nullable = false, length = 255)
    @Enumerated(EnumType.STRING)
    private ProviderType provider;

    @Column(name = "provider_id", length = 255)
    private String providerId;

    @Column
    private LocalDate lastRidingDate;

    @Column
    private Integer consecutiveDays;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    private User(String email, String password, String nickname, UserRole role,
        String image, ProviderType provider, String providerId) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.image = image;
        this.provider = provider;
        this.providerId = providerId;
        this.consecutiveDays = 0;
    }

    public static User of (UserInfo userInfo, ProviderType provider) {
        return User.builder()
            .role(UserRole.USER)
            .email(userInfo.email())
            .image(userInfo.imageUrl())
            .nickname(userInfo.name())
            .provider(provider)
            .providerId(userInfo.providerId())
            .build();
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateImage(String image) {
        this.image = image;
    }

    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void updateRidingStatus() {
        LocalDate today = LocalDate.now();
        LocalDate lastDate = this.lastRidingDate;

        // 마지막 라이딩 기록이 없거나, 연속성이 끊겼을 때
        if (lastDate == null || ChronoUnit.DAYS.between(lastDate, today) > 1) {
            this.consecutiveDays = 1;
        } else if (ChronoUnit.DAYS.between(lastDate, today) == 1) {
            // 연속 라이딩이 이어질 때
            this.consecutiveDays += 1;
        }

        this.lastRidingDate = today;
    }
}
