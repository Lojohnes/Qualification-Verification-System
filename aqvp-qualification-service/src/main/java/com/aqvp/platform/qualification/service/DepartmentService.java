package com.aqvp.platform.qualification.service;

import com.aqvp.platform.qualification.dto.DepartmentRequestDto;
import com.aqvp.platform.qualification.dto.DepartmentResponseDto;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing departments.
 */
public interface DepartmentService {
    DepartmentResponseDto createDepartment(DepartmentRequestDto dto);
    DepartmentResponseDto updateDepartment(UUID id, DepartmentRequestDto dto);
    DepartmentResponseDto getDepartmentById(UUID id);
    List<DepartmentResponseDto> getAllDepartments();
    List<DepartmentResponseDto> getDepartmentsByFaculty(UUID facultyId);
    void deleteDepartment(UUID id);
}
