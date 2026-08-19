package com.aqvp.platform.qualification.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aqvp.platform.qualification.dto.StudentRequestDto;
import com.aqvp.platform.qualification.dto.StudentResponseDto;
import com.aqvp.platform.qualification.dto.StudentUpdateRequestDto;
import com.aqvp.platform.qualification.security.JwtService;
import com.aqvp.platform.qualification.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @MockBean
    private JwtService jwtService;

    private UUID id;
    private UUID institutionId;
    private StudentRequestDto requestDto;
    private StudentResponseDto responseDto;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        institutionId = UUID.randomUUID();
        requestDto = new StudentRequestDto(
            "STU001", "Jane", "Doe", "jane@example.com",
            LocalDate.of(2000, 1, 1), "NID-001", institutionId);
        responseDto = new StudentResponseDto(
            id, "STU001", "Jane", "Doe", "jane@example.com",
            LocalDate.of(2000, 1, 1), "NID-001", institutionId, true,
            null, null, null, null, 1L);
    }

    @Test
    void endpoints_withoutAuth_return401() throws Exception {
        mockMvc.perform(get("/api/v1/students").param("institutionId", institutionId.toString()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "student:write")
    void createStudent_success() throws Exception {
        when(studentService.createStudent(any(StudentRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/students")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.studentNumber").value("STU001"));
    }

    @Test
    @WithMockUser(authorities = "student:write")
    void createStudent_invalidPayload_returnsBadRequest() throws Exception {
        final StudentRequestDto invalidDto = new StudentRequestDto(
            "", "", "", null, null, null, null);

        mockMvc.perform(post("/api/v1/students")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "student:read")
    void getStudentById_success() throws Exception {
        when(studentService.getStudentById(id)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/students/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.studentNumber").value("STU001"));
    }

    @Test
    @WithMockUser(authorities = "student:read")
    void getStudentsByInstitution_success() throws Exception {
        when(studentService.getStudentsByInstitution(institutionId))
            .thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/v1/students").param("institutionId", institutionId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].studentNumber").value("STU001"));
    }

    @Test
    @WithMockUser(authorities = "student:write")
    void updateStudent_success() throws Exception {
        final StudentUpdateRequestDto updateDto = new StudentUpdateRequestDto(
            "Janet", "Doe", "janet@example.com", LocalDate.of(2000, 1, 1), "NID-001");
        when(studentService.updateStudent(any(UUID.class), any(StudentUpdateRequestDto.class)))
            .thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/students/{id}", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "student:write")
    void deactivateStudent_success() throws Exception {
        doNothing().when(studentService).deactivateStudent(id);

        mockMvc.perform(delete("/api/v1/students/{id}", id)
                .with(csrf()))
            .andExpect(status().isNoContent());
    }
}
