package com.aqvp.platform.identity.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.aqvp.platform.identity.domain.Permission;
import com.aqvp.platform.identity.domain.Role;
import com.aqvp.platform.identity.dto.RoleRequestDto;
import com.aqvp.platform.identity.dto.RoleResponseDto;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link RoleMapper}.
 */
class RoleMapperTest {

    private final RoleMapper mapper = new RoleMapperImpl();

    @Test
    void shouldMapRoleRequestDtoToEntity() {
        final RoleRequestDto dto = new RoleRequestDto("ADMIN", "Administrator", Set.of());

        final Role role = mapper.toEntity(dto);

        assertThat(role.getName()).isEqualTo("ADMIN");
        assertThat(role.getDescription()).isEqualTo("Administrator");
        assertThat(role.getPermissions()).isEmpty();
    }

    @Test
    void shouldMapRoleToResponseDto() {
        final Permission permission = Permission.builder()
            .id(UUID.randomUUID())
            .name("user:read")
            .resource("user")
            .action("read")
            .build();
        final Role role = Role.builder()
            .id(UUID.randomUUID())
            .name("USER")
            .description("Standard user")
            .permissions(Set.of(permission))
            .build();

        final RoleResponseDto dto = mapper.toResponseDto(role);

        assertThat(dto.name()).isEqualTo("USER");
        assertThat(dto.permissions()).contains("user:read");
    }

    @Test
    void shouldUpdateEntityFromDto() {
        final Role role = Role.builder().name("OLD").description("Old").build();
        final RoleRequestDto dto = new RoleRequestDto("NEW", "Updated", Set.of());

        mapper.updateEntity(dto, role);

        assertThat(role.getName()).isEqualTo("NEW");
        assertThat(role.getDescription()).isEqualTo("Updated");
    }
}
