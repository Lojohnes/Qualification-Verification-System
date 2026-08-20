package com.aqvp.platform.qualification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Request DTO for updating an existing {@link com.aqvp.platform.qualification.domain.Student}.
 * Student number and institution ID are immutable after creation.
 */
public record StudentUpdateRequestDto(
    @NotBlank(message = "First name is required")
    @Size(max = 150, message = "First name must not exceed 150 characters")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(max = 150, message = "Last name must not exceed 150 characters")
    String lastName,

    @Email(message = "Email must be a valid email address")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    String email,

    LocalDate dateOfBirth,

    @Size(max = 100, message = "National ID must not exceed 100 characters")
    String nationalId
) {}
