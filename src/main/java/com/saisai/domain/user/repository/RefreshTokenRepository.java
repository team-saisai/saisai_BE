package com.saisai.domain.user.repository;

import com.saisai.domain.user.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByProviderId(String providerId);
}
