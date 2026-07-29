package com.aqvp.platform.identity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aqvp.platform.identity.config.JwtConfig;
import com.aqvp.platform.identity.domain.RefreshToken;
import com.aqvp.platform.identity.domain.User;
import com.aqvp.platform.identity.exception.TokenRefreshException;
import com.aqvp.platform.identity.repository.RefreshTokenRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link RefreshTokenServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtConfig jwtConfig;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    @Test
    void shouldCreateRefreshToken() {
        final User user = User.builder().id(UUID.randomUUID()).username("johndoe").build();
        final RefreshToken saved = RefreshToken.builder()
            .id(UUID.randomUUID())
            .token("token-123")
            .expiryDate(LocalDateTime.now().plusDays(7))
            .revoked(false)
            .user(user)
            .build();

        when(jwtConfig.refreshTokenExpirationMs()).thenReturn(604800000L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(saved);

        final RefreshToken result = refreshTokenService.createRefreshToken(user);

        assertThat(result.getToken()).isEqualTo("token-123");
        assertThat(result.getUser()).isEqualTo(user);
    }

    @Test
    void shouldVerifyValidRefreshToken() {
        final RefreshToken token = RefreshToken.builder()
            .token("valid-token")
            .expiryDate(LocalDateTime.now().plusDays(1))
            .revoked(false)
            .build();

        when(refreshTokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));

        final RefreshToken result = refreshTokenService.verifyExpiration("valid-token");

        assertThat(result.getToken()).isEqualTo("valid-token");
    }

    @Test
    void shouldThrowWhenRefreshTokenNotFound() {
        when(refreshTokenRepository.findByToken("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.verifyExpiration("missing"))
            .isInstanceOf(TokenRefreshException.class)
            .hasMessageContaining("not found");
    }

    @Test
    void shouldThrowWhenRefreshTokenIsRevoked() {
        final RefreshToken token = RefreshToken.builder()
            .token("revoked-token")
            .expiryDate(LocalDateTime.now().plusDays(1))
            .revoked(true)
            .build();

        when(refreshTokenRepository.findByToken("revoked-token")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> refreshTokenService.verifyExpiration("revoked-token"))
            .isInstanceOf(TokenRefreshException.class)
            .hasMessageContaining("revoked");
    }

    @Test
    void shouldThrowWhenRefreshTokenIsExpired() {
        final RefreshToken token = RefreshToken.builder()
            .token("expired-token")
            .expiryDate(LocalDateTime.now().minusDays(1))
            .revoked(false)
            .build();

        when(refreshTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> refreshTokenService.verifyExpiration("expired-token"))
            .isInstanceOf(TokenRefreshException.class)
            .hasMessageContaining("expired");
    }

    @Test
    void shouldRevokeTokenByValue() {
        final RefreshToken token = RefreshToken.builder()
            .token("to-revoke")
            .expiryDate(LocalDateTime.now().plusDays(1))
            .revoked(false)
            .user(User.builder().username("johndoe").build())
            .build();

        when(refreshTokenRepository.findByToken("to-revoke")).thenReturn(Optional.of(token));
        when(refreshTokenRepository.save(token)).thenReturn(token);

        refreshTokenService.revokeByToken("to-revoke");

        assertThat(token.getRevoked()).isTrue();
        verify(refreshTokenRepository).save(token);
    }

    @Test
    void shouldRevokeAllTokensForUser() {
        final User user = User.builder().username("johndoe").build();
        final RefreshToken token = RefreshToken.builder()
            .token("token-1")
            .expiryDate(LocalDateTime.now().plusDays(1))
            .revoked(false)
            .user(user)
            .build();

        when(refreshTokenRepository.findByUserAndRevokedFalse(user)).thenReturn(List.of(token));
        when(refreshTokenRepository.save(token)).thenReturn(token);

        refreshTokenService.revokeAllByUser(user);

        assertThat(token.getRevoked()).isTrue();
        verify(refreshTokenRepository).save(token);
    }
}
