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
import java.time.LocalDateTime;
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

    @Column(name = "name", nullable = false, length = 10)
    private String nickname;

    @Column(name = "role", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "provider", nullable = false, length = 255)
    @Enumerated(EnumType.STRING)
    private ProviderType provider;

    @Column(name = "provider_id", length = 255)
    private String providerId;

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
}
