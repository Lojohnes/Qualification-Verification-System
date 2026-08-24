package com.aqvp.platform.verification.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.aqvp.platform.verification.domain.ConsentScope;
import com.aqvp.platform.verification.domain.ConsentStatus;
import com.aqvp.platform.verification.domain.ConsentType;
import com.aqvp.platform.verification.dto.ConsentRequestDto;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ConsentValidationServiceTest {

    private final ConsentValidationService service = new ConsentValidationService();

    @Test
    void validateAcceptsCurrentAttestedConsentWithReference() {
        final ConsentRequestDto consent = new ConsentRequestDto(
            ConsentType.ATTESTED_BY_VERIFIER,
            ConsentScope.BASIC_DETAILS,
            null,
            null,
            null,
            null,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(10),
            "HR-123"
        );

        final ConsentValidationResult result = service.validate(consent);

        assertThat(result.status()).isEqualTo(ConsentStatus.VALID);
        assertThat(result.validForDisclosure()).isTrue();
    }

    @Test
    void validateRejectsAttestedConsentWithoutReference() {
        final ConsentRequestDto consent = new ConsentRequestDto(
            ConsentType.ATTESTED_BY_VERIFIER,
            ConsentScope.BASIC_DETAILS,
            null,
            null,
            null,
            null,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(10),
            null
        );

        final ConsentValidationResult result = service.validate(consent);

        assertThat(result.status()).isEqualTo(ConsentStatus.INVALID);
        assertThat(result.validForDisclosure()).isFalse();
    }

    @Test
    void validateMarksMissingConsentAsPendingStatusOnly() {
        final ConsentValidationResult result = service.validate(null);

        assertThat(result.status()).isEqualTo(ConsentStatus.PENDING);
        assertThat(result.scope()).isEqualTo(ConsentScope.STATUS_ONLY);
        assertThat(result.validForDisclosure()).isFalse();
        assertThat(result.validForVerification()).isFalse();
    }
}
