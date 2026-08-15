package com.aqvp.platform.qualification.service;

import com.aqvp.platform.qualification.domain.Department;
import com.aqvp.platform.qualification.domain.Institution;
import com.aqvp.platform.qualification.domain.Program;
import com.aqvp.platform.qualification.dto.ProgramRequestDto;
import com.aqvp.platform.qualification.dto.ProgramResponseDto;
import com.aqvp.platform.qualification.exception.DuplicateResourceException;
import com.aqvp.platform.qualification.exception.EntityNotFoundException;
import com.aqvp.platform.qualification.mapper.ProgramMapper;
import com.aqvp.platform.qualification.repository.DepartmentRepository;
import com.aqvp.platform.qualification.repository.InstitutionRepository;
import com.aqvp.platform.qualification.repository.ProgramRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing academic programs.
 */
@Service
@RequiredArgsConstructor
public class ProgramServiceImpl implements ProgramService {

    private final ProgramRepository programRepository;
    private final InstitutionRepository institutionRepository;
    private final DepartmentRepository departmentRepository;
    private final ProgramMapper programMapper;

    @Override
    @Transactional
    public ProgramResponseDto createProgram(ProgramRequestDto dto) {
        if (programRepository.existsByCode(dto.code())) {
            throw new DuplicateResourceException("Program code already exists: " + dto.code());
        }

        final Institution institution = institutionRepository.findById(dto.institutionId())
            .orElseThrow(() -> new EntityNotFoundException("Institution not found with id: " + dto.institutionId()));

        final Department department = departmentRepository.findById(dto.departmentId())
            .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + dto.departmentId()));

        final Program program = programMapper.toEntity(dto);
        program.setInstitution(institution);
        program.setDepartment(department);

        final Program savedProgram = programRepository.save(program);
        return programMapper.toResponseDto(savedProgram);
    }

    @Override
    @Transactional
    public ProgramResponseDto updateProgram(UUID id, ProgramRequestDto dto) {
        final Program program = programRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Program not found with id: " + id));

        if (!program.getCode().equals(dto.code()) && programRepository.existsByCode(dto.code())) {
            throw new DuplicateResourceException("Program code already exists: " + dto.code());
        }

        final Institution institution = institutionRepository.findById(dto.institutionId())
            .orElseThrow(() -> new EntityNotFoundException("Institution not found with id: " + dto.institutionId()));

        final Department department = departmentRepository.findById(dto.departmentId())
            .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + dto.departmentId()));

        programMapper.updateEntity(dto, program);
        program.setInstitution(institution);
        program.setDepartment(department);

        final Program updatedProgram = programRepository.save(program);
        return programMapper.toResponseDto(updatedProgram);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramResponseDto getProgramById(UUID id) {
        final Program program = programRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Program not found with id: " + id));
        return programMapper.toResponseDto(program);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramResponseDto> getAllPrograms() {
        return programRepository.findAll().stream()
            .map(programMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramResponseDto> getProgramsByInstitution(UUID institutionId) {
        return programRepository.findByInstitutionId(institutionId).stream()
            .map(programMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteProgram(UUID id) {
        final Program program = programRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Program not found with id: " + id));
        programRepository.delete(program);
    }
}
