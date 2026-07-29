package com.aqvp.platform.identity.repository;

import com.aqvp.platform.identity.domain.Permission;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Permission} aggregate persistence.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    Optional<Permission> findByName(String name);

    List<Permission> findAllByOrderByNameAsc();
}
