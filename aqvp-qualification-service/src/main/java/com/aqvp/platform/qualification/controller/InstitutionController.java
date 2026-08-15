package com.aqvp.platform.qualification.controller;

import com.aqvp.platform.qualification.dto.InstitutionRequestDto;
import com.aqvp.platform.qualification.dto.InstitutionResponseDto;
import com.aqvp.platform.qualification.service.InstitutionService;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing institutions.
 */
@RestController
@RequestMapping("/api/v1/institutions")
@RequiredArgsConstructor
public class InstitutionController {

    private final InstitutionService institutionService;

    @PostMapping
    public ResponseEntity<InstitutionResponseDto> createInstitution(@Valid @RequestBody InstitutionRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(institutionService.createInstitution(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstitutionResponseDto> updateInstitution(
            @PathVariable UUID id,
            @Valid @RequestBody InstitutionRequestDto dto) {
        return ResponseEntity.ok(institutionService.updateInstitution(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstitutionResponseDto> getInstitutionById(@PathVariable UUID id) {
        return ResponseEntity.ok(institutionService.getInstitutionById(id));
    }

    @GetMapping
    public ResponseEntity<List<InstitutionResponseDto>> getAllInstitutions() {
        return ResponseEntity.ok(institutionService.getAllInstitutions());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateInstitution(@PathVariable UUID id) {
        institutionService.deactivateInstitution(id);
        return ResponseEntity.noContent().build();
    }
}
