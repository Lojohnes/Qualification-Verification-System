package com.aqvp.platform.qualification.mapper;

import com.aqvp.platform.qualification.domain.QualificationDocument;
import com.aqvp.platform.qualification.dto.DocumentResponseDto;
import org.springframework.stereotype.Component;

/**
 * Maps generated document metadata to API DTOs.
 */
@Component
public class DocumentMapper {

    public DocumentResponseDto toResponseDto(QualificationDocument document) {
        return new DocumentResponseDto(
                document.getId(),
                document.getQualificationId(),
                document.getDocumentType().name(),
                document.getFileName(),
                document.getContentType(),
                document.getSizeBytes(),
                document.getSha256Hash(),
                document.getQrPayload(),
                document.getDigitalSignature(),
                document.getSignatureAlgorithm(),
                document.getSignerKeyId(),
                document.getGeneratedAt());
    }
}
