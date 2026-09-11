package com.aqvp.platform.qualification.dto;

import java.util.List;

/** Result of validating or confirming a qualification CSV import. */
public record BulkImportResponseDto(
    int totalRows,
    int validRows,
    int importedRows,
    List<BulkImportErrorDto> errors
) {}