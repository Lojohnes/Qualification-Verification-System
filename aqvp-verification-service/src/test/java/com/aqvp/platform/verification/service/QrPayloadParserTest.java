package com.aqvp.platform.verification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.aqvp.platform.verification.exception.InvalidQrException;
import org.junit.jupiter.api.Test;

class QrPayloadParserTest {

    private final QrPayloadParser parser = new QrPayloadParser();

    @Test
    void parseReturnsStructuredPayloadForValidQr() {
        final QrPayload payload = parser.parse("AQVP:v1:MSU:security-123");

        assertThat(payload.version()).isEqualTo("v1");
        assertThat(payload.issuerCode()).isEqualTo("MSU");
        assertThat(payload.securityIdentifier()).isEqualTo("security-123");
    }

    @Test
    void parseRejectsMalformedQr() {
        assertThatThrownBy(() -> parser.parse("not-a-valid-qr"))
            .isInstanceOf(InvalidQrException.class)
            .hasMessageContaining("AQVP:v1");
    }

    @Test
    void parseRejectsUnsupportedVersion() {
        assertThatThrownBy(() -> parser.parse("AQVP:v2:MSU:security-123"))
            .isInstanceOf(InvalidQrException.class)
            .hasMessageContaining("Unsupported");
    }
}
