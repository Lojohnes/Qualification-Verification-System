package com.aqvp.platform.qualification.service;

import com.aqvp.platform.qualification.dto.DocumentFileDto;
import com.aqvp.platform.qualification.dto.DocumentResponseDto;
import java.util.List;
import java.util.UUID;

/**
 * Service for qualification documents and QR codes.
 */
public interface DocumentService {

    byte[] generateCertificate(UUID qualificationId);

    byte[] generateTranscript(UUID qualificationId);

    byte[] generateQrCode(UUID qualificationId);

    DocumentResponseDto generateCertificateDocument(UUID qualificationId);

    DocumentResponseDto generateTranscriptDocument(UUID qualificationId);

    DocumentResponseDto generateQrCodeDocument(UUID qualificationId);

    List<DocumentResponseDto> getDocumentsForQualification(UUID qualificationId);

    DocumentResponseDto getDocument(UUID documentId);

    DocumentFileDto getDocumentFile(UUID documentId);
}
