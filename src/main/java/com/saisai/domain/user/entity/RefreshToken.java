package com.saisai.domain.user.entity;

import com.saisai.domain.auth.constant.ProviderType;
import com.saisai.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    @Id
    private String providerId;

    @Enumerated(EnumType.STRING)
    private ProviderType provider;

    @Column(nullable = false)
    private String token;


    public RefreshToken(String providerId, ProviderType provider, String token) {
        this.providerId = providerId;
        this.provider = provider;
        this.token = token;
    }
}
