package com.aqvp.platform.qualification.controller;

import com.aqvp.platform.qualification.dto.BulkImportResponseDto;
import com.aqvp.platform.qualification.service.BulkQualificationImportService;
import java.security.Principal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** REST endpoints for previewing and confirming qualification CSV imports. */
@RestController
@RequestMapping("/api/v1/qualifications/bulk-import")
@RequiredArgsConstructor
public class BulkQualificationImportController {

    private final BulkQualificationImportService importService;

    @PostMapping(value = "/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BulkImportResponseDto> preview(
            @RequestParam UUID institutionId, @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(importService.preview(file, institutionId));
    }

    @PostMapping(value = "/confirm", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BulkImportResponseDto> confirm(
            @RequestParam UUID institutionId, @RequestPart("file") MultipartFile file, Principal principal) {
        final String importedBy = principal == null ? "system" : principal.getName();
        return ResponseEntity.ok(importService.confirm(file, institutionId, importedBy));
    }
}