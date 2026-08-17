package com.aqvp.platform.qualification.service;

import com.aqvp.platform.qualification.dto.FacultyRequestDto;
import com.aqvp.platform.qualification.dto.FacultyResponseDto;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing faculties.
 */
public interface FacultyService {
    FacultyResponseDto createFaculty(FacultyRequestDto dto);
    FacultyResponseDto updateFaculty(UUID id, FacultyRequestDto dto);
    FacultyResponseDto getFacultyById(UUID id);
    List<FacultyResponseDto> getAllFaculties();
    List<FacultyResponseDto> getFacultiesByInstitution(UUID institutionId);
    void deleteFaculty(UUID id);
}
