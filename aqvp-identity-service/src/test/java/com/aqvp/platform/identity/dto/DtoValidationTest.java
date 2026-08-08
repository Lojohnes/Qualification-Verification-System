package com.aqvp.platform.identity.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Standalone bean validation tests for DTO records.
 */
class DtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldRejectBlankAuthenticationRequest() {
        final AuthenticationRequest request = new AuthenticationRequest("", "");

        final Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(2);
    }

    @Test
    void shouldAcceptValidAuthenticationRequest() {
        final AuthenticationRequest request = new AuthenticationRequest("johndoe", "Password123!");

        final Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldRejectBlankRefreshTokenRequest() {
        final RefreshTokenRequest request = new RefreshTokenRequest("");

        final Set<ConstraintViolation<RefreshTokenRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
    }

    @Test
    void shouldAcceptValidRefreshTokenRequest() {
        final RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");

        final Set<ConstraintViolation<RefreshTokenRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldRejectInvalidEmailInUserUpdateRequest() {
        final UserUpdateRequestDto request = new UserUpdateRequestDto(
            "not-an-email", "John", "Doe", true, Set.of(UUID.randomUUID())
        );

        final Set<ConstraintViolation<UserUpdateRequestDto>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
    }

    @Test
    void shouldRejectWeakPasswordInChangePasswordRequest() {
        final ChangePasswordRequest request = new ChangePasswordRequest(
            "current", "weak", "weak"
        );

        final Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
    }

    @Test
    void shouldRejectBlankApiClientRequest() {
        final ApiClientRequestDto request = new ApiClientRequestDto(
            "", "", "", null, true, Set.of()
        );

        final Set<ConstraintViolation<ApiClientRequestDto>> violations = validator.validate(request);

        assertThat(violations).hasSize(3);
    }
}
