package com.aqvp.platform.qualification.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aqvp.platform.qualification.dto.DocumentFileDto;
import com.aqvp.platform.qualification.dto.DocumentResponseDto;
import com.aqvp.platform.qualification.security.JwtService;
import com.aqvp.platform.qualification.service.DocumentService;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DocumentController.class)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @MockBean
    private JwtService jwtService;

    private UUID qualificationId;
    private UUID documentId;
    private DocumentResponseDto responseDto;

    @BeforeEach
    void setUp() {
        qualificationId = UUID.randomUUID();
        documentId = UUID.randomUUID();
        responseDto = new DocumentResponseDto(
                documentId,
                qualificationId,
                "CERTIFICATE",
                "certificate-QUAL-001.pdf",
                "application/pdf",
                10L,
                "hash",
                "AQVP:v1:MSU:sec-123",
                "signature",
                "HmacSHA256",
                "test-key",
                LocalDateTime.of(2026, 8, 24, 14, 0));
    }

    @Test
    void endpoints_withoutAuth_return401() throws Exception {
        mockMvc.perform(get("/api/v1/qualifications/{id}/documents", qualificationId))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "qualification:read")
    void generateCertificate_returnsPdf() throws Exception {
        when(documentService.generateCertificate(qualificationId))
                .thenReturn("%PDF".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(get("/api/v1/qualifications/{id}/certificate", qualificationId))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/pdf"))
            .andExpect(header().string("Content-Disposition",
                    "inline; filename=\"certificate-" + qualificationId + ".pdf\""));
    }

    @Test
    @WithMockUser(authorities = "qualification:read")
    void generateCertificateMetadata_returnsCreatedMetadata() throws Exception {
        when(documentService.generateCertificateDocument(qualificationId)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/qualifications/{id}/certificate/metadata", qualificationId))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.documentType").value("CERTIFICATE"))
            .andExpect(jsonPath("$.qrPayload").value("AQVP:v1:MSU:sec-123"))
            .andExpect(jsonPath("$.signatureAlgorithm").value("HmacSHA256"));
    }

    @Test
    @WithMockUser(authorities = "qualification:read")
    void getDocumentsForQualification_returnsMetadataList() throws Exception {
        when(documentService.getDocumentsForQualification(qualificationId)).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/v1/qualifications/{id}/documents", qualificationId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].fileName").value("certificate-QUAL-001.pdf"));
    }

    @Test
    @WithMockUser(authorities = "qualification:read")
    void downloadDocument_returnsStoredFile() throws Exception {
        final byte[] content = "%PDF".getBytes(StandardCharsets.UTF_8);
        when(documentService.getDocumentFile(documentId))
                .thenReturn(new DocumentFileDto("certificate-QUAL-001.pdf", "application/pdf", content));

        mockMvc.perform(get("/api/v1/qualifications/documents/{documentId}/download", documentId))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/pdf"))
            .andExpect(header().string("Content-Disposition",
                    "attachment; filename=\"certificate-QUAL-001.pdf\""))
            .andExpect(content().bytes(content));
    }
}
