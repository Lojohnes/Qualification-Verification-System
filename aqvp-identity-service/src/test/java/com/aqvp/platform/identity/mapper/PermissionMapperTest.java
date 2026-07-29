package com.aqvp.platform.identity.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.aqvp.platform.identity.domain.Permission;
import com.aqvp.platform.identity.dto.PermissionDto;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link PermissionMapper}.
 */
class PermissionMapperTest {

    private final PermissionMapper mapper = new PermissionMapperImpl();

    @Test
    void shouldMapPermissionToDto() {
        final Permission permission = Permission.builder()
            .id(UUID.randomUUID())
            .name("user:read")
            .resource("user")
            .action("read")
            .description("Read users")
            .build();

        final PermissionDto dto = mapper.toDto(permission);

        assertThat(dto.id()).isEqualTo(permission.getId());
        assertThat(dto.name()).isEqualTo("user:read");
        assertThat(dto.resource()).isEqualTo("user");
        assertThat(dto.action()).isEqualTo("read");
        assertThat(dto.description()).isEqualTo("Read users");
    }
}
