package com.aqvp.platform.qualification.service;

import com.aqvp.platform.qualification.domain.Faculty;
import com.aqvp.platform.qualification.domain.Institution;
import com.aqvp.platform.qualification.dto.FacultyRequestDto;
import com.aqvp.platform.qualification.dto.FacultyResponseDto;
import com.aqvp.platform.qualification.exception.DuplicateResourceException;
import com.aqvp.platform.qualification.exception.EntityNotFoundException;
import com.aqvp.platform.qualification.mapper.FacultyMapper;
import com.aqvp.platform.qualification.repository.FacultyRepository;
import com.aqvp.platform.qualification.repository.InstitutionRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing faculties.
 */
@Service
@RequiredArgsConstructor
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;
    private final InstitutionRepository institutionRepository;
    private final FacultyMapper facultyMapper;

    @Override
    @Transactional
    public FacultyResponseDto createFaculty(FacultyRequestDto dto) {
        final Institution institution = institutionRepository.findById(dto.institutionId())
            .orElseThrow(() -> new EntityNotFoundException("Institution not found with id: " + dto.institutionId()));

        if (facultyRepository.existsByInstitutionIdAndCode(dto.institutionId(), dto.code())) {
            throw new DuplicateResourceException("Faculty code already exists for this institution: " + dto.code());
        }

        final Faculty faculty = facultyMapper.toEntity(dto);
        faculty.setInstitution(institution);
        final Faculty savedFaculty = facultyRepository.save(faculty);
        return facultyMapper.toResponseDto(savedFaculty);
    }

    @Override
    @Transactional
    public FacultyResponseDto updateFaculty(UUID id, FacultyRequestDto dto) {
        final Faculty faculty = facultyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Faculty not found with id: " + id));

        if (!faculty.getCode().equals(dto.code())
                && facultyRepository.existsByInstitutionIdAndCode(dto.institutionId(), dto.code())) {
            throw new DuplicateResourceException("Faculty code already exists for this institution: " + dto.code());
        }

        final Institution institution = institutionRepository.findById(dto.institutionId())
            .orElseThrow(() -> new EntityNotFoundException("Institution not found with id: " + dto.institutionId()));

        facultyMapper.updateEntity(dto, faculty);
        faculty.setInstitution(institution);
        final Faculty updatedFaculty = facultyRepository.save(faculty);
        return facultyMapper.toResponseDto(updatedFaculty);
    }

    @Override
    @Transactional(readOnly = true)
    public FacultyResponseDto getFacultyById(UUID id) {
        final Faculty faculty = facultyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Faculty not found with id: " + id));
        return facultyMapper.toResponseDto(faculty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacultyResponseDto> getAllFaculties() {
        return facultyRepository.findAll().stream()
            .map(facultyMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacultyResponseDto> getFacultiesByInstitution(UUID institutionId) {
        return facultyRepository.findByInstitutionId(institutionId).stream()
            .map(facultyMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteFaculty(UUID id) {
        final Faculty faculty = facultyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Faculty not found with id: " + id));
        facultyRepository.delete(faculty);
    }
}
