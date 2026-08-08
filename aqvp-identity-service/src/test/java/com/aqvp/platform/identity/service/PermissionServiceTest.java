package com.aqvp.platform.identity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.aqvp.platform.identity.domain.Permission;
import com.aqvp.platform.identity.dto.PermissionDto;
import com.aqvp.platform.identity.exception.EntityNotFoundException;
import com.aqvp.platform.identity.mapper.PermissionMapper;
import com.aqvp.platform.identity.repository.PermissionRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link PermissionServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private PermissionMapper permissionMapper;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    @Test
    void shouldFindPermissionById() {
        final UUID id = UUID.randomUUID();
        final Permission permission = Permission.builder()
            .id(id)
            .name("user:read")
            .resource("user")
            .action("read")
            .build();

        when(permissionRepository.findById(id)).thenReturn(Optional.of(permission));
        when(permissionMapper.toDto(permission)).thenReturn(
            new PermissionDto(id, "user:read", "user", "read", null)
        );

        final PermissionDto result = permissionService.findById(id);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.name()).isEqualTo("user:read");
    }

    @Test
    void shouldThrowWhenPermissionNotFoundById() {
        final UUID id = UUID.randomUUID();
        when(permissionRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> permissionService.findById(id))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Permission not found");
    }

    @Test
    void shouldFindAllPermissions() {
        final Permission permission = Permission.builder().id(UUID.randomUUID()).name("user:read").build();
        when(permissionRepository.findAllByOrderByNameAsc()).thenReturn(List.of(permission));
        when(permissionMapper.toDto(permission)).thenReturn(
            new PermissionDto(permission.getId(), "user:read", null, null, null)
        );

        final List<PermissionDto> result = permissionService.findAll();

        assertThat(result).hasSize(1);
    }
}
