package com.aqvp.platform.qualification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Represents a student (qualification holder) registered within an institution.
 */
@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Student extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String studentNumber;

    @Column(nullable = false, length = 150)
    private String firstName;

    @Column(nullable = false, length = 150)
    private String lastName;

    @Column(unique = true, length = 255)
    private String email;

    @Column
    private LocalDate dateOfBirth;

    @Column(length = 100)
    private String nationalId;

    /**
     * UUID reference to the owning institution.
     * Stored as a plain UUID FK; no JPA cross-entity navigation to preserve module boundaries.
     */
    @Column(nullable = false)
    private UUID institutionId;

    @Column(nullable = false)
    private Boolean active = true;
}
