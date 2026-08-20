package com.aqvp.platform.qualification.service;

import java.util.UUID;

/**
 * Service for qualification documents and QR codes.
 */
public interface DocumentService {

    byte[] generateCertificate(UUID qualificationId);

    byte[] generateTranscript(UUID qualificationId);

    byte[] generateQrCode(UUID qualificationId);
}
