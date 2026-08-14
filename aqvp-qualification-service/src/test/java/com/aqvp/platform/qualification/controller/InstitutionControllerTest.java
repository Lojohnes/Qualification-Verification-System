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

import com.aqvp.platform.qualification.dto.InstitutionRequestDto;
import com.aqvp.platform.qualification.dto.InstitutionResponseDto;
import com.aqvp.platform.qualification.security.JwtService;
import com.aqvp.platform.qualification.service.InstitutionService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@WebMvcTest(InstitutionController.class)
class InstitutionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InstitutionService institutionService;

    @MockBean
    private JwtService jwtService;

    private UUID id;
    private InstitutionRequestDto requestDto;
    private InstitutionResponseDto responseDto;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        requestDto = new InstitutionRequestDto("Test University", "TESTU", "A test university", true);
        responseDto = new InstitutionResponseDto(id, "Test University", "TESTU", "A test university", true, null, null, null, null, 1L);
    }

    @Test
    void endpoints_withoutAuth_return401() throws Exception {
        mockMvc.perform(get("/api/v1/institutions"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "institution:write")
    void createInstitution_success() throws Exception {
        when(institutionService.createInstitution(any(InstitutionRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/institutions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.name").value("Test University"))
            .andExpect(jsonPath("$.code").value("TESTU"));
    }

    @Test
    @WithMockUser(authorities = "institution:write")
    void createInstitution_invalidPayload_returnsBadRequest() throws Exception {
        final InstitutionRequestDto invalidDto = new InstitutionRequestDto("", "", "A test university", true);

        mockMvc.perform(post("/api/v1/institutions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "institution:read")
    void getInstitutionById_success() throws Exception {
        when(institutionService.getInstitutionById(id)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/institutions/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.code").value("TESTU"));
    }

    @Test
    @WithMockUser(authorities = "institution:read")
    void getAllInstitutions_success() throws Exception {
        when(institutionService.getAllInstitutions()).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/v1/institutions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].code").value("TESTU"));
    }

    @Test
    @WithMockUser(authorities = "institution:write")
    void updateInstitution_success() throws Exception {
        when(institutionService.updateInstitution(any(UUID.class), any(InstitutionRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/institutions/{id}", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("TESTU"));
    }

    @Test
    @WithMockUser(authorities = "institution:write")
    void deactivateInstitution_success() throws Exception {
        doNothing().when(institutionService).deactivateInstitution(id);

        mockMvc.perform(delete("/api/v1/institutions/{id}", id)
                .with(csrf()))
            .andExpect(status().isNoContent());
    }
}
