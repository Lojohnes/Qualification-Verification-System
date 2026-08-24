package com.aqvp.platform.verification.dto;

import com.aqvp.platform.verification.domain.ConsentScope;
import com.aqvp.platform.verification.domain.ConsentType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Consent evidence supplied for a verification request.
 */
public record ConsentRequestDto(
    @NotNull ConsentType consentType,
    @NotNull ConsentScope scope,
    String holderFirstName,
    String holderLastName,
    LocalDate dateOfBirth,
    String holderEmail,
    LocalDateTime grantedAt,
    LocalDateTime expiresAt,
    String consentReference
) {}
