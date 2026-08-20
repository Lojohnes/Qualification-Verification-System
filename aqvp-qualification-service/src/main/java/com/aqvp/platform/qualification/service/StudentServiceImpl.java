package com.aqvp.platform.qualification.service;

import com.aqvp.platform.qualification.domain.Student;
import com.aqvp.platform.qualification.dto.StudentRequestDto;
import com.aqvp.platform.qualification.dto.StudentResponseDto;
import com.aqvp.platform.qualification.dto.StudentUpdateRequestDto;
import com.aqvp.platform.qualification.exception.DuplicateResourceException;
import com.aqvp.platform.qualification.exception.EntityNotFoundException;
import com.aqvp.platform.qualification.mapper.StudentMapper;
import com.aqvp.platform.qualification.repository.StudentRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing student records.
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Override
    @Transactional
    public StudentResponseDto createStudent(StudentRequestDto dto) {
        if (studentRepository.existsByStudentNumber(dto.studentNumber())) {
            throw new DuplicateResourceException("Student number already exists: " + dto.studentNumber());
        }
        if (dto.email() != null && !dto.email().isBlank() && studentRepository.existsByEmail(dto.email())) {
            throw new DuplicateResourceException("Email already registered: " + dto.email());
        }
        final Student student = studentMapper.toEntity(dto);
        student.setActive(true);
        final Student saved = studentRepository.save(student);
        return studentMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public StudentResponseDto updateStudent(UUID id, StudentUpdateRequestDto dto) {
        final Student student = studentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));

        if (dto.email() != null && !dto.email().isBlank()
                && !dto.email().equals(student.getEmail())
                && studentRepository.existsByEmail(dto.email())) {
            throw new DuplicateResourceException("Email already registered: " + dto.email());
        }

        studentMapper.updateEntity(dto, student);
        final Student updated = studentRepository.save(student);
        return studentMapper.toResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDto getStudentById(UUID id) {
        final Student student = studentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
        return studentMapper.toResponseDto(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponseDto> getStudentsByInstitution(UUID institutionId) {
        return studentRepository.findByInstitutionIdAndActiveTrue(institutionId).stream()
            .map(studentMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deactivateStudent(UUID id) {
        final Student student = studentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
        student.setActive(false);
        studentRepository.save(student);
    }
}
