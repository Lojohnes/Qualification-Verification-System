package com.aqvp.platform.qualification.service;

import com.aqvp.platform.qualification.dto.ProgramRequestDto;
import com.aqvp.platform.qualification.dto.ProgramResponseDto;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing academic programs.
 */
public interface ProgramService {
    ProgramResponseDto createProgram(ProgramRequestDto dto);
    ProgramResponseDto updateProgram(UUID id, ProgramRequestDto dto);
    ProgramResponseDto getProgramById(UUID id);
    List<ProgramResponseDto> getAllPrograms();
    List<ProgramResponseDto> getProgramsByInstitution(UUID institutionId);
    void deleteProgram(UUID id);
}
