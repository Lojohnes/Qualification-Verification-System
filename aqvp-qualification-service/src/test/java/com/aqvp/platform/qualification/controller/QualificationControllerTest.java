package com.aqvp.platform.qualification.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aqvp.platform.qualification.dto.QualificationAmendRequestDto;
import com.aqvp.platform.qualification.dto.QualificationRequestDto;
import com.aqvp.platform.qualification.dto.QualificationResponseDto;
import com.aqvp.platform.qualification.dto.QualificationRevokeRequestDto;
import com.aqvp.platform.qualification.security.JwtService;
import com.aqvp.platform.qualification.service.QualificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(QualificationController.class)
class QualificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QualificationService qualificationService;

    @MockBean
    private JwtService jwtService;

    private UUID id;
    private UUID studentId;
    private UUID institutionId;
    private QualificationRequestDto requestDto;
    private QualificationResponseDto responseDto;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        studentId = UUID.randomUUID();
        institutionId = UUID.randomUUID();

        requestDto = new QualificationRequestDto(
            "QUAL-001", studentId, institutionId, null,
            "DEGREE", "Bachelor of Science", "First Class", 2024, null);

        responseDto = new QualificationResponseDto(
            id, "QUAL-001", studentId, institutionId, null,
            "DEGREE", "Bachelor of Science", "First Class",
            2024, "DRAFT", null, null, null, null, null,
            List.of(), null, null, null, null, 1L);
    }

    @Test
    void endpoints_withoutAuth_return401() throws Exception {
        mockMvc.perform(get("/api/v1/qualifications").param("institutionId", institutionId.toString()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "qualification:write")
    void createQualification_success() throws Exception {
        when(qualificationService.createQualification(any(QualificationRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/qualifications")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.qualificationNumber").value("QUAL-001"))
            .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    @WithMockUser(authorities = "qualification:write")
    void createQualification_invalidPayload_returnsBadRequest() throws Exception {
        final QualificationRequestDto invalidDto = new QualificationRequestDto(
            "", null, null, null, null, "", null, null, null);

        mockMvc.perform(post("/api/v1/qualifications")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "qualification:read")
    void getQualificationById_success() throws Exception {
        when(qualificationService.getQualificationById(id)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/qualifications/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.qualificationNumber").value("QUAL-001"));
    }

    @Test
    @WithMockUser(authorities = "qualification:read")
    void getQualificationsByInstitution_success() throws Exception {
        when(qualificationService.getQualificationsByInstitution(institutionId))
            .thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/v1/qualifications").param("institutionId", institutionId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].qualificationNumber").value("QUAL-001"));
    }

    @Test
    @WithMockUser(authorities = "qualification:read")
    void getQualificationsByStudent_success() throws Exception {
        when(qualificationService.getQualificationsByStudent(studentId))
            .thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/v1/qualifications/by-student/{studentId}", studentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].qualificationNumber").value("QUAL-001"));
    }

    @Test
    @WithMockUser(authorities = "qualification:write")
    void updateQualification_success() throws Exception {
        when(qualificationService.updateQualification(any(UUID.class), any(QualificationRequestDto.class)))
            .thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/qualifications/{id}", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.qualificationNumber").value("QUAL-001"));
    }

    @Test
    @WithMockUser(authorities = "qualification:write")
    void issueQualification_success() throws Exception {
        final QualificationResponseDto issuedResponse = new QualificationResponseDto(
            id, "QUAL-001", studentId, institutionId, null,
            "DEGREE", "Bachelor of Science", "First Class",
            2024, "ISSUED", "sec-id-123", null, null, null, null,
            List.of(), null, null, null, null, 2L);

        when(qualificationService.issueQualification(eq(id), any(), any())).thenReturn(issuedResponse);

        mockMvc.perform(post("/api/v1/qualifications/{id}/issue", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ISSUED"))
            .andExpect(jsonPath("$.securityIdentifier").value("sec-id-123"));
    }

    @Test
    @WithMockUser(authorities = "qualification:write")
    void amendQualification_success() throws Exception {
        final QualificationAmendRequestDto amendDto = new QualificationAmendRequestDto(
            "Correcting name", "Bachelor of Science (Hons)", "First Class Honours", null);
        when(qualificationService.amendQualification(eq(id), any(QualificationAmendRequestDto.class), any()))
            .thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/qualifications/{id}/amend", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(amendDto)))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "qualification:write")
    void revokeQualification_success() throws Exception {
        final QualificationRevokeRequestDto revokeDto = new QualificationRevokeRequestDto("Fraudulent record");
        when(qualificationService.revokeQualification(eq(id), any(QualificationRevokeRequestDto.class), any()))
            .thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/qualifications/{id}/revoke", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(revokeDto)))
            .andExpect(status().isOk());
    }
}
