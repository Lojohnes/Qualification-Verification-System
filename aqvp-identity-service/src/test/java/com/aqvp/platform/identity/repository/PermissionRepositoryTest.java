package com.aqvp.platform.identity.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.aqvp.platform.identity.domain.Permission;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Repository tests for {@link Permission} persistence.
 */
@DataJpaTest
@ActiveProfiles("test")
class PermissionRepositoryTest {

    @Autowired
    private PermissionRepository permissionRepository;

    @Test
    void shouldSaveAndFindPermissionByName() {
        final Permission permission = Permission.builder()
            .name("z:action")
            .resource("z")
            .action("action")
            .build();
        permissionRepository.save(permission);

        final Optional<Permission> found = permissionRepository.findByName("z:action");

        assertThat(found).isPresent();
        assertThat(found.get().getResource()).isEqualTo("z");
    }

    @Test
    void shouldReturnPermissionsOrderedByName() {
        permissionRepository.save(Permission.builder().name("b:read").resource("b").action("read").build());
        permissionRepository.save(Permission.builder().name("a:read").resource("a").action("read").build());

        final List<Permission> permissions = permissionRepository.findAllByOrderByNameAsc();

        assertThat(permissions).hasSize(2);
        assertThat(permissions.get(0).getName()).isEqualTo("a:read");
        assertThat(permissions.get(1).getName()).isEqualTo("b:read");
    }
}
