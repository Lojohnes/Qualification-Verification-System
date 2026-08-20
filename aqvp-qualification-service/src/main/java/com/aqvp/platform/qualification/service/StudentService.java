package com.aqvp.platform.qualification.service;

import com.aqvp.platform.qualification.dto.StudentRequestDto;
import com.aqvp.platform.qualification.dto.StudentResponseDto;
import com.aqvp.platform.qualification.dto.StudentUpdateRequestDto;
import java.util.List;
import java.util.UUID;

/**
 * Service contract for managing student records.
 */
public interface StudentService {

    StudentResponseDto createStudent(StudentRequestDto dto);

    StudentResponseDto updateStudent(UUID id, StudentUpdateRequestDto dto);

    StudentResponseDto getStudentById(UUID id);

    List<StudentResponseDto> getStudentsByInstitution(UUID institutionId);

    void deactivateStudent(UUID id);
}
