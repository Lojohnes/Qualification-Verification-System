package com.aqvp.platform.qualification.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * DTO record for creating or updating a {@link com.aqvp.platform.qualification.domain.Program}.
 */
public record ProgramRequestDto(
    @NotNull(message = "Institution ID is required")
    UUID institutionId,

    @NotNull(message = "Department ID is required")
    UUID departmentId,

    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    String name,

    @NotBlank(message = "Code is required")
    @Size(max = 30, message = "Code must not exceed 30 characters")
    String code,

    @Size(max = 50, message = "Degree level must not exceed 50 characters")
    String degreeLevel,

    @Min(value = 1, message = "Duration must be at least 1 semester")
    Integer durationSemesters
) {}
