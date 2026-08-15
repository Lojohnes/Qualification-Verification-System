package com.aqvp.platform.qualification.service;

import com.aqvp.platform.qualification.dto.InstitutionRequestDto;
import com.aqvp.platform.qualification.dto.InstitutionResponseDto;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing institutions.
 */
public interface InstitutionService {
    InstitutionResponseDto createInstitution(InstitutionRequestDto dto);
    InstitutionResponseDto updateInstitution(UUID id, InstitutionRequestDto dto);
    InstitutionResponseDto getInstitutionById(UUID id);
    List<InstitutionResponseDto> getAllInstitutions();
    void deactivateInstitution(UUID id);
}
