package com.aqvp.platform.qualification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * DTO record for creating or updating a {@link com.aqvp.platform.qualification.domain.Department}.
 */
public record DepartmentRequestDto(
    @NotNull(message = "Faculty ID is required")
    UUID facultyId,

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    String name,

    @NotBlank(message = "Code is required")
    @Size(max = 20, message = "Code must not exceed 20 characters")
    String code
) {}
