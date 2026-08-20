package com.aqvp.platform.qualification.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Request DTO for creating or updating a {@link com.aqvp.platform.qualification.domain.Qualification}.
 *
 * <p>Status transitions (issue, amend, revoke) use dedicated action endpoints, not this DTO.
 */
public record QualificationRequestDto(
    @NotBlank(message = "Qualification number is required")
    @Size(max = 100, message = "Qualification number must not exceed 100 characters")
    String qualificationNumber,

    @NotNull(message = "Student ID is required")
    UUID studentId,

    @NotNull(message = "Institution ID is required")
    UUID institutionId,

    UUID programId,

    @NotNull(message = "Qualification type is required")
    String qualificationType,

    @NotBlank(message = "Qualification name is required")
    @Size(max = 255, message = "Qualification name must not exceed 255 characters")
    String qualificationName,

    @Size(max = 100, message = "Classification must not exceed 100 characters")
    String classification,

    @NotNull(message = "Year of award is required")
    @Min(value = 1900, message = "Year of award must be 1900 or later")
    @Max(value = 2200, message = "Year of award must be 2200 or earlier")
    Integer yearOfAward,

    String notes
) {}
