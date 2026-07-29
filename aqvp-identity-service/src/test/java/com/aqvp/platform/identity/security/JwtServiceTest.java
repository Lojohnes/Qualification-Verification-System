package com.aqvp.platform.identity.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.aqvp.platform.identity.config.JwtConfig;
import com.aqvp.platform.identity.domain.User;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link JwtService}.
 */
@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtConfig jwtConfig;

    @InjectMocks
    private JwtService jwtService;

    @Test
    void shouldGenerateAndValidateAccessToken() {
        final String secret = "my-256-bit-secret-key-for-unit-testing-jwt-service-123456";
        when(jwtConfig.secret()).thenReturn(secret);
        when(jwtConfig.accessTokenExpirationMs()).thenReturn(10000L);

        final User user = User.builder()
            .id(UUID.randomUUID())
            .username("johndoe")
            .email("john@aqvp.local")
            .password("encoded")
            .build();
        final UserPrincipal principal = UserPrincipal.of(user);

        final String token = jwtService.generateAccessToken(principal);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("johndoe");
        assertThat(jwtService.isTokenValid(token, principal)).isTrue();
    }
}
