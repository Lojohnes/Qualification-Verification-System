package com.aqvp.platform.qualification.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aqvp.platform.qualification.domain.Student;
import com.aqvp.platform.qualification.dto.StudentRequestDto;
import com.aqvp.platform.qualification.dto.StudentResponseDto;
import com.aqvp.platform.qualification.dto.StudentUpdateRequestDto;
import com.aqvp.platform.qualification.exception.DuplicateResourceException;
import com.aqvp.platform.qualification.exception.EntityNotFoundException;
import com.aqvp.platform.qualification.mapper.StudentMapper;
import com.aqvp.platform.qualification.repository.StudentRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentServiceImpl studentService;

    private UUID id;
    private UUID institutionId;
    private StudentRequestDto requestDto;
    private Student student;
    private StudentResponseDto responseDto;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        institutionId = UUID.randomUUID();
        requestDto = new StudentRequestDto(
            "STU001", "Jane", "Doe", "jane@example.com",
            LocalDate.of(2000, 1, 1), "NID-001", institutionId);
        student = Student.builder()
            .id(id)
            .studentNumber("STU001")
            .firstName("Jane")
            .lastName("Doe")
            .email("jane@example.com")
            .institutionId(institutionId)
            .active(true)
            .build();
        responseDto = new StudentResponseDto(
            id, "STU001", "Jane", "Doe", "jane@example.com",
            LocalDate.of(2000, 1, 1), "NID-001", institutionId, true,
            null, null, null, null, 1L);
    }

    @Test
    void createStudent_success() {
        when(studentRepository.existsByStudentNumber("STU001")).thenReturn(false);
        when(studentRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(studentMapper.toEntity(requestDto)).thenReturn(student);
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toResponseDto(student)).thenReturn(responseDto);

        final StudentResponseDto result = studentService.createStudent(requestDto);

        assertNotNull(result);
        assertEquals("STU001", result.studentNumber());
        verify(studentRepository).save(student);
    }

    @Test
    void createStudent_duplicateStudentNumber_throwsException() {
        when(studentRepository.existsByStudentNumber("STU001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> studentService.createStudent(requestDto));
        verify(studentRepository, never()).save(any());
    }

    @Test
    void createStudent_duplicateEmail_throwsException() {
        when(studentRepository.existsByStudentNumber("STU001")).thenReturn(false);
        when(studentRepository.existsByEmail("jane@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> studentService.createStudent(requestDto));
        verify(studentRepository, never()).save(any());
    }

    @Test
    void getStudentById_success() {
        when(studentRepository.findById(id)).thenReturn(Optional.of(student));
        when(studentMapper.toResponseDto(student)).thenReturn(responseDto);

        final StudentResponseDto result = studentService.getStudentById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
    }

    @Test
    void getStudentById_notFound_throwsException() {
        when(studentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> studentService.getStudentById(id));
    }

    @Test
    void updateStudent_success() {
        final StudentUpdateRequestDto updateDto = new StudentUpdateRequestDto(
            "Janet", "Doe", "janet@example.com", LocalDate.of(2000, 1, 1), "NID-001");
        final StudentResponseDto updatedResponse = new StudentResponseDto(
            id, "STU001", "Janet", "Doe", "janet@example.com",
            LocalDate.of(2000, 1, 1), "NID-001", institutionId, true,
            null, null, null, null, 2L);

        when(studentRepository.findById(id)).thenReturn(Optional.of(student));
        when(studentRepository.existsByEmail("janet@example.com")).thenReturn(false);
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toResponseDto(student)).thenReturn(updatedResponse);

        final StudentResponseDto result = studentService.updateStudent(id, updateDto);

        assertNotNull(result);
        assertEquals("Janet", result.firstName());
        verify(studentMapper).updateEntity(updateDto, student);
    }

    @Test
    void getStudentsByInstitution_returnsList() {
        when(studentRepository.findByInstitutionIdAndActiveTrue(institutionId))
            .thenReturn(List.of(student));
        when(studentMapper.toResponseDto(student)).thenReturn(responseDto);

        final List<StudentResponseDto> results = studentService.getStudentsByInstitution(institutionId);

        assertEquals(1, results.size());
    }

    @Test
    void deactivateStudent_success() {
        when(studentRepository.findById(id)).thenReturn(Optional.of(student));
        when(studentRepository.save(student)).thenReturn(student);

        studentService.deactivateStudent(id);

        assertFalse(student.getActive());
        verify(studentRepository).save(student);
    }
}
