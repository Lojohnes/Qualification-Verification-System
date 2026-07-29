package com.aqvp.platform.identity.repository;

import com.aqvp.platform.identity.domain.ApiClient;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link ApiClient} persistence.
 */
@Repository
public interface ApiClientRepository extends JpaRepository<ApiClient, UUID> {

    Optional<ApiClient> findByClientId(String clientId);

    boolean existsByClientId(String clientId);
}
