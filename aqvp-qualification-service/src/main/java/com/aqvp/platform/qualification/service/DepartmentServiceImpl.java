package com.aqvp.platform.qualification.service;

import com.aqvp.platform.qualification.domain.Department;
import com.aqvp.platform.qualification.domain.Faculty;
import com.aqvp.platform.qualification.dto.DepartmentRequestDto;
import com.aqvp.platform.qualification.dto.DepartmentResponseDto;
import com.aqvp.platform.qualification.exception.DuplicateResourceException;
import com.aqvp.platform.qualification.exception.EntityNotFoundException;
import com.aqvp.platform.qualification.mapper.DepartmentMapper;
import com.aqvp.platform.qualification.repository.DepartmentRepository;
import com.aqvp.platform.qualification.repository.FacultyRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing departments.
 */
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    @Transactional
    public DepartmentResponseDto createDepartment(DepartmentRequestDto dto) {
        final Faculty faculty = facultyRepository.findById(dto.facultyId())
            .orElseThrow(() -> new EntityNotFoundException("Faculty not found with id: " + dto.facultyId()));

        if (departmentRepository.existsByFacultyIdAndCode(dto.facultyId(), dto.code())) {
            throw new DuplicateResourceException("Department code already exists for this faculty: " + dto.code());
        }

        final Department department = departmentMapper.toEntity(dto);
        department.setFaculty(faculty);
        final Department savedDepartment = departmentRepository.save(department);
        return departmentMapper.toResponseDto(savedDepartment);
    }

    @Override
    @Transactional
    public DepartmentResponseDto updateDepartment(UUID id, DepartmentRequestDto dto) {
        final Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + id));

        if (!department.getCode().equals(dto.code())
                && departmentRepository.existsByFacultyIdAndCode(dto.facultyId(), dto.code())) {
            throw new DuplicateResourceException("Department code already exists for this faculty: " + dto.code());
        }

        final Faculty faculty = facultyRepository.findById(dto.facultyId())
            .orElseThrow(() -> new EntityNotFoundException("Faculty not found with id: " + dto.facultyId()));

        departmentMapper.updateEntity(dto, department);
        department.setFaculty(faculty);
        final Department updatedDepartment = departmentRepository.save(department);
        return departmentMapper.toResponseDto(updatedDepartment);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponseDto getDepartmentById(UUID id) {
        final Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + id));
        return departmentMapper.toResponseDto(department);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponseDto> getAllDepartments() {
        return departmentRepository.findAll().stream()
            .map(departmentMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponseDto> getDepartmentsByFaculty(UUID facultyId) {
        return departmentRepository.findByFacultyId(facultyId).stream()
            .map(departmentMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteDepartment(UUID id) {
        final Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + id));
        departmentRepository.delete(department);
    }
}
