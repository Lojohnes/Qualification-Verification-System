package com.aqvp.platform.identity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.aqvp.platform.identity.domain.RefreshToken;
import com.aqvp.platform.identity.domain.User;
import com.aqvp.platform.identity.dto.AuthenticationRequest;
import com.aqvp.platform.identity.dto.AuthenticationResponse;
import com.aqvp.platform.identity.repository.UserRepository;
import com.aqvp.platform.identity.security.JwtService;
import com.aqvp.platform.identity.security.UserPrincipal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Unit tests for {@link AuthServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldAuthenticateUserAndReturnTokens() {
        final User user = User.builder()
            .id(UUID.randomUUID())
            .username("johndoe")
            .email("john@aqvp.local")
            .password("encoded")
            .build();
        final UserPrincipal principal = UserPrincipal.of(user);
        final Authentication authentication = new UsernamePasswordAuthenticationToken(
            principal, null, principal.getAuthorities());
        final AuthenticationRequest request = new AuthenticationRequest("johndoe", "Password123!");
        final RefreshToken refreshToken = RefreshToken.builder()
            .token(UUID.randomUUID().toString())
            .expiryDate(LocalDateTime.now().plusDays(7))
            .user(user)
            .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(jwtService.generateAccessToken(principal)).thenReturn("access-token");
        when(jwtService.getAccessExpirationMs()).thenReturn(900000L);
        when(refreshTokenService.createRefreshToken(user)).thenReturn(refreshToken);

        final AuthenticationResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo(refreshToken.getToken());
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(900L);
    }
}
