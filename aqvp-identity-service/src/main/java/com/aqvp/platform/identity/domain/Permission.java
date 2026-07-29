package com.aqvp.platform.identity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * A fine-grained permission bound to a resource and action.
 */
@Entity
@Table(
    name = "permissions",
    uniqueConstraints = @UniqueConstraint(columnNames = {"name"})
)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Permission extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String resource;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(length = 500)
    private String description;

    @Builder.Default
    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();
}
