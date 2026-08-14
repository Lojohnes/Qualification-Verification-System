package com.aqvp.platform.identity.service;

import com.aqvp.platform.identity.domain.RefreshToken;
import com.aqvp.platform.identity.domain.User;
import com.aqvp.platform.identity.dto.AuthenticationRequest;
import com.aqvp.platform.identity.dto.AuthenticationResponse;
import com.aqvp.platform.identity.dto.ChangePasswordRequest;
import com.aqvp.platform.identity.dto.ForgotPasswordRequest;
import com.aqvp.platform.identity.dto.RefreshTokenRequest;
import com.aqvp.platform.identity.dto.ResetPasswordRequest;
import com.aqvp.platform.identity.dto.UserResponseDto;
import com.aqvp.platform.identity.exception.EntityNotFoundException;
import com.aqvp.platform.identity.exception.InvalidCredentialsException;
import com.aqvp.platform.identity.exception.PasswordMismatchException;
import com.aqvp.platform.identity.mapper.UserMapper;
import com.aqvp.platform.identity.repository.UserRepository;
import com.aqvp.platform.identity.security.JwtService;
import com.aqvp.platform.identity.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements authentication, token refresh and password management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public AuthenticationResponse login(AuthenticationRequest dto) {
        try {
            final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.usernameOrEmail(), dto.password())
            );
            final UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            final User user = principal.getUser();
            final String accessToken = jwtService.generateAccessToken(principal);
            final RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            log.info("User '{}' logged in successfully", user.getUsername());
            return new AuthenticationResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                jwtService.getAccessExpirationMs() / 1000
            );
        } catch (AuthenticationException ex) {
            log.warn("Failed login attempt for '{}'", dto.usernameOrEmail());
            throw new InvalidCredentialsException("Invalid username/email or password");
        }
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequest dto) {
        refreshTokenService.revokeByToken(dto.refreshToken());
        log.info("User logged out and refresh token revoked");
    }

    @Override
    @Transactional(readOnly = true)
    public AuthenticationResponse refresh(RefreshTokenRequest dto) {
        final RefreshToken refreshToken = refreshTokenService.verifyExpiration(dto.refreshToken());
        final User user = refreshToken.getUser();
        final UserPrincipal principal = UserPrincipal.of(user);
        final String accessToken = jwtService.generateAccessToken(principal);
        log.info("Access token refreshed for user '{}'", user.getUsername());
        return new AuthenticationResponse(
            accessToken,
            refreshToken.getToken(),
            "Bearer",
            jwtService.getAccessExpirationMs() / 1000
        );
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void changePassword(ChangePasswordRequest dto) {
        final String username = SecurityContextHolder.getContext().getAuthentication().getName();
        final User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        if (!dto.newPassword().equals(dto.confirmPassword())) {
            throw new PasswordMismatchException("New password and confirmation do not match");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        refreshTokenService.revokeAllByUser(user);
        userRepository.save(user);
        log.info("Password changed for user '{}'", username);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest dto) {
        log.info("Password reset requested for email '{}' - placeholder implementation", dto.email());
    }

    @Override
    public void resetPassword(ResetPasswordRequest dto) {
        if (!dto.newPassword().equals(dto.confirmPassword())) {
            throw new PasswordMismatchException("New password and confirmation do not match");
        }
        log.info("Password reset requested with token '{}' - placeholder implementation", dto.token());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public UserResponseDto getCurrentUser() {
        final String username = SecurityContextHolder.getContext().getAuthentication().getName();
        final User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        return userMapper.toResponseDto(user);
    }
}
