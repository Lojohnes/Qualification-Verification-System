package com.aqvp.platform.identity.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.aqvp.platform.identity.domain.Permission;
import com.aqvp.platform.identity.domain.Role;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

/**
 * Repository tests for {@link Role} persistence.
 */
@DataJpaTest
@ActiveProfiles("test")
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldSaveAndFindRoleByName() {
        final Role role = Role.builder().name("TEST_ROLE").description("Test").build();
        roleRepository.save(role);

        final Optional<Role> found = roleRepository.findByName("TEST_ROLE");

        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("Test");
    }

    @Test
    void shouldCheckRoleExistenceByName() {
        final Role role = Role.builder().name("EXISTING_ROLE").build();
        roleRepository.save(role);

        assertThat(roleRepository.existsByName("EXISTING_ROLE")).isTrue();
        assertThat(roleRepository.existsByName("MISSING_ROLE")).isFalse();
    }

    @Test
    void shouldPersistRoleWithPermissions() {
        final Permission permission = Permission.builder()
            .name("test:read")
            .resource("test")
            .action("read")
            .build();
        entityManager.persist(permission);

        final Role role = Role.builder()
            .name("ROLE_WITH_PERMISSION")
            .permissions(Set.of(permission))
            .build();
        roleRepository.save(role);
        entityManager.flush();
        entityManager.clear();

        final Optional<Role> found = roleRepository.findByName("ROLE_WITH_PERMISSION");

        assertThat(found).isPresent();
        assertThat(found.get().getPermissions()).hasSize(1);
        assertThat(found.get().getPermissions().iterator().next().getName()).isEqualTo("test:read");
    }
}
