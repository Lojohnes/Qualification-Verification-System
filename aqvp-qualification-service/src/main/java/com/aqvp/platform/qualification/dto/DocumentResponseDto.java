package com.aqvp.platform.qualification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Generated document metadata returned by document endpoints.
 */
public record DocumentResponseDto(
    UUID id,
    UUID qualificationId,
    String documentType,
    String fileName,
    String contentType,
    Long sizeBytes,
    String sha256Hash,
    String qrPayload,
    String digitalSignature,
    String signatureAlgorithm,
    String signerKeyId,
    LocalDateTime generatedAt
) {}
