package com.aqvp.platform.qualification.dto;

/** Describes a validation error for one CSV row. */
public record BulkImportErrorDto(int row, String field, String message) {}