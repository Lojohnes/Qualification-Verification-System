package com.aqvp.platform.qualification.controller;

import com.aqvp.platform.qualification.dto.FacultyRequestDto;
import com.aqvp.platform.qualification.dto.FacultyResponseDto;
import com.aqvp.platform.qualification.service.FacultyService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing faculties.
 */
@RestController
@RequestMapping("/api/v1/faculties")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    @PostMapping
    public ResponseEntity<FacultyResponseDto> createFaculty(@Valid @RequestBody FacultyRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facultyService.createFaculty(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacultyResponseDto> updateFaculty(
            @PathVariable UUID id,
            @Valid @RequestBody FacultyRequestDto dto) {
        return ResponseEntity.ok(facultyService.updateFaculty(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacultyResponseDto> getFacultyById(@PathVariable UUID id) {
        return ResponseEntity.ok(facultyService.getFacultyById(id));
    }

    @GetMapping
    public ResponseEntity<List<FacultyResponseDto>> getAllFaculties(
            @RequestParam(required = false) UUID institutionId) {
        if (institutionId != null) {
            return ResponseEntity.ok(facultyService.getFacultiesByInstitution(institutionId));
        }
        return ResponseEntity.ok(facultyService.getAllFaculties());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable UUID id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.noContent().build();
    }
}
