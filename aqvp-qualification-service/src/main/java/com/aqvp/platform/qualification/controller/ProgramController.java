package com.aqvp.platform.qualification.controller;

import com.aqvp.platform.qualification.dto.ProgramRequestDto;
import com.aqvp.platform.qualification.dto.ProgramResponseDto;
import com.aqvp.platform.qualification.service.ProgramService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
 * REST controller for managing academic programs.
 */
@RestController
@RequestMapping("/api/v1/programs")
@RequiredArgsConstructor
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification = "Spring service bean injection")
public class ProgramController {

    private final ProgramService programService;

    @PostMapping
    public ResponseEntity<ProgramResponseDto> createProgram(@Valid @RequestBody ProgramRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(programService.createProgram(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProgramResponseDto> updateProgram(
            @PathVariable UUID id,
            @Valid @RequestBody ProgramRequestDto dto) {
        return ResponseEntity.ok(programService.updateProgram(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgramResponseDto> getProgramById(@PathVariable UUID id) {
        return ResponseEntity.ok(programService.getProgramById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProgramResponseDto>> getAllPrograms(
            @RequestParam(required = false) UUID institutionId) {
        if (institutionId != null) {
            return ResponseEntity.ok(programService.getProgramsByInstitution(institutionId));
        }
        return ResponseEntity.ok(programService.getAllPrograms());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgram(@PathVariable UUID id) {
        programService.deleteProgram(id);
        return ResponseEntity.noContent().build();
    }
}
