package com.aqvp.platform.identity.repository;

import com.aqvp.platform.identity.domain.Role;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Role} aggregate persistence.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(String name);

    boolean existsByName(String name);
}
