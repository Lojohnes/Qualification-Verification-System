package com.aqvp.platform.verification.service;

import com.aqvp.platform.verification.exception.InvalidQrException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Parses AQVP QR payloads.
 */
@Component
public class QrPayloadParser {

    private static final String PREFIX = "AQVP";
    private static final String SUPPORTED_VERSION = "v1";

    public QrPayload parse(String qrPayload) {
        if (!StringUtils.hasText(qrPayload)) {
            throw new InvalidQrException("QR payload is required");
        }

        final String[] parts = qrPayload.trim().split(":", -1);
        if (parts.length != 4 || !PREFIX.equals(parts[0])) {
            throw new InvalidQrException("QR payload must match AQVP:v1:<issuerCode>:<securityIdentifier>");
        }
        if (!SUPPORTED_VERSION.equals(parts[1])) {
            throw new InvalidQrException("Unsupported AQVP QR version: " + parts[1]);
        }
        if (!StringUtils.hasText(parts[2])) {
            throw new InvalidQrException("QR issuer code is required");
        }
        if (!StringUtils.hasText(parts[3]) || parts[3].length() > 255) {
            throw new InvalidQrException("QR security identifier is invalid");
        }
        return new QrPayload(parts[1], parts[2], parts[3]);
    }
}
