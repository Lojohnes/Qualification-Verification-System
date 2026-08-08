package com.aqvp.platform.identity.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
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
 * Stores credentials and authorities for machine-to-machine API clients.
 */
@Entity
@Table(
    name = "api_clients",
    uniqueConstraints = @UniqueConstraint(columnNames = {"client_id"})
)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ApiClient extends BaseEntity {

    @Column(name = "client_id", nullable = false, unique = true, length = 255)
    private String clientId;

    @Column(name = "client_secret_hash", nullable = false, length = 255)
    private String clientSecretHash;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 500)
    private String description;

    @Builder.Default
    @Column(nullable = false)
    private Boolean enabled = true;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "api_client_authorities", joinColumns = @JoinColumn(name = "api_client_id"))
    @Column(name = "authority")
    private Set<String> authorities = new HashSet<>();
}
