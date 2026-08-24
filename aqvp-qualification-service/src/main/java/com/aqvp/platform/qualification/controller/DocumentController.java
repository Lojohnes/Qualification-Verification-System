package com.aqvp.platform.qualification.controller;

import com.aqvp.platform.qualification.dto.DocumentFileDto;
import com.aqvp.platform.qualification.dto.DocumentResponseDto;
import com.aqvp.platform.qualification.service.DocumentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for qualification documents and QR codes.
 */
@RestController
@RequestMapping("/api/v1/qualifications")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/{id}/certificate")
    public ResponseEntity<byte[]> generateCertificate(@PathVariable UUID id) {
        final byte[] pdf = documentService.generateCertificate(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"certificate-" + id + ".pdf\"")
                .body(pdf);
    }

    @GetMapping("/{id}/certificate/metadata")
    public ResponseEntity<DocumentResponseDto> generateCertificateMetadata(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentService.generateCertificateDocument(id));
    }

    @GetMapping("/{id}/transcript")
    public ResponseEntity<byte[]> generateTranscript(@PathVariable UUID id) {
        final byte[] pdf = documentService.generateTranscript(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"transcript-" + id + ".pdf\"")
                .body(pdf);
    }

    @GetMapping("/{id}/transcript/metadata")
    public ResponseEntity<DocumentResponseDto> generateTranscriptMetadata(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentService.generateTranscriptDocument(id));
    }

    @GetMapping("/{id}/qr")
    public ResponseEntity<byte[]> generateQrCode(@PathVariable UUID id) {
        final byte[] png = documentService.generateQrCode(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"qr-" + id + ".png\"")
                .body(png);
    }

    @GetMapping("/{id}/qr/metadata")
    public ResponseEntity<DocumentResponseDto> generateQrCodeMetadata(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentService.generateQrCodeDocument(id));
    }

    @GetMapping("/{id}/documents")
    public ResponseEntity<List<DocumentResponseDto>> getDocumentsForQualification(@PathVariable UUID id) {
        return ResponseEntity.ok(documentService.getDocumentsForQualification(id));
    }

    @GetMapping("/documents/{documentId}")
    public ResponseEntity<DocumentResponseDto> getDocument(@PathVariable UUID documentId) {
        return ResponseEntity.ok(documentService.getDocument(documentId));
    }

    @GetMapping("/documents/{documentId}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable UUID documentId) {
        final DocumentFileDto document = documentService.getDocumentFile(documentId);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(document.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.fileName() + "\"")
                .body(document.content());
    }
}
