package com.aqvp.platform.qualification.service;

import com.aqvp.platform.qualification.domain.Institution;
import com.aqvp.platform.qualification.dto.InstitutionRequestDto;
import com.aqvp.platform.qualification.dto.InstitutionResponseDto;
import com.aqvp.platform.qualification.exception.DuplicateResourceException;
import com.aqvp.platform.qualification.exception.EntityNotFoundException;
import com.aqvp.platform.qualification.mapper.InstitutionMapper;
import com.aqvp.platform.qualification.repository.InstitutionRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing institutions.
 */
@Service
@RequiredArgsConstructor
public class InstitutionServiceImpl implements InstitutionService {

    private final InstitutionRepository institutionRepository;
    private final InstitutionMapper institutionMapper;

    @Override
    @Transactional
    public InstitutionResponseDto createInstitution(InstitutionRequestDto dto) {
        if (institutionRepository.existsByCode(dto.code())) {
            throw new DuplicateResourceException("Institution code already exists: " + dto.code());
        }
        final Institution institution = institutionMapper.toEntity(dto);
        if (dto.active() == null) {
            institution.setActive(true);
        }
        final Institution savedInstitution = institutionRepository.save(institution);
        return institutionMapper.toResponseDto(savedInstitution);
    }

    @Override
    @Transactional
    public InstitutionResponseDto updateInstitution(UUID id, InstitutionRequestDto dto) {
        final Institution institution = institutionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Institution not found with id: " + id));

        if (!institution.getCode().equals(dto.code()) && institutionRepository.existsByCode(dto.code())) {
            throw new DuplicateResourceException("Institution code already exists: " + dto.code());
        }

        institutionMapper.updateEntity(dto, institution);
        if (dto.active() != null) {
            institution.setActive(dto.active());
        }
        final Institution updatedInstitution = institutionRepository.save(institution);
        return institutionMapper.toResponseDto(updatedInstitution);
    }

    @Override
    @Transactional(readOnly = true)
    public InstitutionResponseDto getInstitutionById(UUID id) {
        final Institution institution = institutionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Institution not found with id: " + id));
        return institutionMapper.toResponseDto(institution);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstitutionResponseDto> getAllInstitutions() {
        return institutionRepository.findAll().stream()
            .map(institutionMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deactivateInstitution(UUID id) {
        final Institution institution = institutionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Institution not found with id: " + id));
        institution.setActive(false);
        institutionRepository.save(institution);
    }
}
