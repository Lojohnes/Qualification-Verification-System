package com.aqvp.platform.qualification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Request DTO for creating a new {@link com.aqvp.platform.qualification.domain.Student}.
 */
public record StudentRequestDto(
    @NotBlank(message = "Student number is required")
    @Size(max = 100, message = "Student number must not exceed 100 characters")
    String studentNumber,

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
    String nationalId,

    @NotNull(message = "Institution ID is required")
    UUID institutionId
) {}
