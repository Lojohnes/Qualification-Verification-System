package com.aqvp.platform.identity.service;

import com.aqvp.platform.identity.config.JwtConfig;
import com.aqvp.platform.identity.domain.RefreshToken;
import com.aqvp.platform.identity.domain.User;
import com.aqvp.platform.identity.exception.TokenRefreshException;
import com.aqvp.platform.identity.repository.RefreshTokenRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements refresh token creation, expiration validation and revocation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtConfig jwtConfig;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        final RefreshToken token = RefreshToken.builder()
            .token(UUID.randomUUID().toString())
            .expiryDate(LocalDateTime.now().plusNanos(jwtConfig.refreshTokenExpirationMs() * 1_000_000L))
            .revoked(false)
            .user(user)
            .build();
        final RefreshToken saved = refreshTokenRepository.save(token);
        log.info("Created refresh token for user '{}'", user.getUsername());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken verifyExpiration(String token) {
        final RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
            .orElseThrow(() -> new TokenRefreshException("Refresh token not found"));

        if (refreshToken.getRevoked()) {
            throw new TokenRefreshException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TokenRefreshException("Refresh token has expired");
        }

        return refreshToken;
    }

    @Override
    @Transactional
    public void revokeByToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshToken -> {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            log.info("Revoked refresh token for user '{}'", refreshToken.getUser().getUsername());
        });
    }

    @Override
    @Transactional
    public void revokeAllByUser(User user) {
        refreshTokenRepository.findByUserAndRevokedFalse(user).forEach(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
        log.info("Revoked all refresh tokens for user '{}'", user.getUsername());
    }
}
