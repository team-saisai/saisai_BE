package com.saisai.domain.user.service;

import static com.saisai.domain.common.exception.ExceptionCode.INVALID_REFRESH_TOKEN;

import com.saisai.domain.auth.constant.ProviderType;
import com.saisai.domain.user.entity.RefreshToken;
import com.saisai.domain.user.repository.RefreshTokenRepository;
import com.saisai.domain.common.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Transactional
    public void saveRefreshToken(String providerId, ProviderType provider, String token) {
        repository.save(new RefreshToken(providerId, provider, token));
    }

    public String getRefreshToken(String providerId) {
        return repository.findByProviderId (providerId)
            .map(RefreshToken::getToken)
            .orElseThrow(() -> new CustomException(INVALID_REFRESH_TOKEN));
    }

    @Transactional
    public void deleteRefreshToken(String providerId) {
        repository.deleteById(providerId);
    }
}
