package com.aqvp.platform.qualification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Metadata for generated qualification document artifacts stored outside the database.
 */
@Entity
@Table(name = "qualification_documents")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class QualificationDocument extends BaseEntity {

    @Column(nullable = false)
    private UUID qualificationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DocumentType documentType;

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false, length = 500)
    private String storageKey;

    @Column(name = "sha256_hash", nullable = false, length = 64)
    private String sha256Hash;

    @Column(nullable = false)
    private Long sizeBytes;

    @Column(nullable = false, length = 500)
    private String qrPayload;

    @Column(nullable = false, length = 255)
    private String digitalSignature;

    @Column(nullable = false, length = 100)
    private String signatureAlgorithm;

    @Column(nullable = false, length = 100)
    private String signerKeyId;

    @Column(nullable = false)
    private LocalDateTime generatedAt;
}
