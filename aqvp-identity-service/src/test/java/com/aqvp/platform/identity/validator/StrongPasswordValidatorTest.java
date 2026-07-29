package com.aqvp.platform.identity.validator;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link StrongPasswordValidator}.
 */
class StrongPasswordValidatorTest {

    private final StrongPasswordValidator validator = new StrongPasswordValidator();
    private final ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);

    @Test
    void shouldAcceptNullOrBlankValue() {
        assertThat(validator.isValid(null, context)).isTrue();
        assertThat(validator.isValid("   ", context)).isTrue();
    }

    @Test
    void shouldAcceptStrongPassword() {
        assertThat(validator.isValid("Password123!", context)).isTrue();
        assertThat(validator.isValid("My$ecret8", context)).isTrue();
    }

    @Test
    void shouldRejectWeakPasswords() {
        assertThat(validator.isValid("short1!", context)).isFalse();
        assertThat(validator.isValid("Password123", context)).isFalse();
        assertThat(validator.isValid("password123!", context)).isFalse();
        assertThat(validator.isValid("PASSWORD123!", context)).isFalse();
        assertThat(validator.isValid("Password!!!", context)).isFalse();
    }
}
