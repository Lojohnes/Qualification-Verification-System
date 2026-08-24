package com.aqvp.platform.verification.service;

/**
 * Parsed AQVP QR payload.
 */
public record QrPayload(
    String version,
    String issuerCode,
    String securityIdentifier
) {}
