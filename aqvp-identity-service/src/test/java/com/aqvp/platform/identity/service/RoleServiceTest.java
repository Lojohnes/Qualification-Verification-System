package com.aqvp.platform.identity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aqvp.platform.identity.domain.Permission;
import com.aqvp.platform.identity.domain.Role;
import com.aqvp.platform.identity.dto.RoleRequestDto;
import com.aqvp.platform.identity.dto.RoleResponseDto;
import com.aqvp.platform.identity.exception.EntityNotFoundException;
import com.aqvp.platform.identity.mapper.RoleMapper;
import com.aqvp.platform.identity.repository.PermissionRepository;
import com.aqvp.platform.identity.repository.RoleRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link RoleServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void shouldCreateRoleAndReturnResponse() {
        final UUID permissionId = UUID.randomUUID();
        final Permission permission = Permission.builder().id(permissionId).name("user:read").build();
        final RoleRequestDto request = new RoleRequestDto("MANAGER", "Manager role", Set.of(permissionId));
        final Role roleEntity = Role.builder().name(request.name()).description(request.description()).build();
        final Role saved = Role.builder()
            .id(UUID.randomUUID())
            .name(request.name())
            .permissions(Set.of(permission))
            .build();

        when(roleRepository.existsByName(request.name())).thenReturn(false);
        when(roleMapper.toEntity(request)).thenReturn(roleEntity);
        when(permissionRepository.findAllById(request.permissionIds())).thenReturn(List.of(permission));
        when(roleRepository.save(roleEntity)).thenReturn(saved);
        when(roleMapper.toResponseDto(saved)).thenReturn(
            new RoleResponseDto(saved.getId(), saved.getName(), saved.getDescription(), Set.of("user:read"))
        );

        final RoleResponseDto response = roleService.createRole(request);

        assertThat(response.name()).isEqualTo("MANAGER");
        assertThat(response.permissions()).contains("user:read");
        verify(roleRepository).save(roleEntity);
    }

    @Test
    void shouldThrowWhenCreatingDuplicateRole() {
        final RoleRequestDto request = new RoleRequestDto("ADMIN", null, Set.of());
        when(roleRepository.existsByName(request.name())).thenReturn(true);

        assertThatThrownBy(() -> roleService.createRole(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Role already exists");
    }

    @Test
    void shouldUpdateRoleAndReturnResponse() {
        final UUID roleId = UUID.randomUUID();
        final UUID permissionId = UUID.randomUUID();
        final Permission permission = Permission.builder().id(permissionId).name("role:read").build();
        final Role existing = Role.builder().id(roleId).name("ADMIN").build();
        final RoleRequestDto request = new RoleRequestDto("ADMIN", "Updated", Set.of(permissionId));
        final Role saved = Role.builder().id(roleId).name("ADMIN").permissions(Set.of(permission)).build();

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existing));
        when(permissionRepository.findAllById(request.permissionIds())).thenReturn(List.of(permission));
        when(roleRepository.save(existing)).thenReturn(saved);
        when(roleMapper.toResponseDto(saved)).thenReturn(
            new RoleResponseDto(saved.getId(), saved.getName(), saved.getDescription(), Set.of("role:read"))
        );

        final RoleResponseDto response = roleService.updateRole(roleId, request);

        assertThat(response.id()).isEqualTo(roleId);
        assertThat(response.permissions()).contains("role:read");
        verify(roleMapper).updateEntity(request, existing);
    }

    @Test
    void shouldThrowWhenUpdatingMissingRole() {
        final UUID roleId = UUID.randomUUID();
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.updateRole(roleId, new RoleRequestDto("X", null, Set.of())))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Role not found");
    }

    @Test
    void shouldFindRoleById() {
        final UUID roleId = UUID.randomUUID();
        final Role role = Role.builder().id(roleId).name("USER").build();

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(roleMapper.toResponseDto(role)).thenReturn(
            new RoleResponseDto(roleId, "USER", null, Set.of())
        );

        final RoleResponseDto response = roleService.findById(roleId);

        assertThat(response.id()).isEqualTo(roleId);
    }

    @Test
    void shouldThrowWhenRoleNotFoundById() {
        final UUID roleId = UUID.randomUUID();
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.findById(roleId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Role not found");
    }

    @Test
    void shouldFindAllRoles() {
        final Role role = Role.builder().id(UUID.randomUUID()).name("USER").build();
        when(roleRepository.findAll()).thenReturn(List.of(role));
        when(roleMapper.toResponseDto(role)).thenReturn(new RoleResponseDto(role.getId(), "USER", null, Set.of()));

        final List<RoleResponseDto> result = roleService.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldThrowWhenPermissionDoesNotExist() {
        final UUID permissionId = UUID.randomUUID();
        final RoleRequestDto request = new RoleRequestDto("R", null, Set.of(permissionId));

        when(roleRepository.existsByName(request.name())).thenReturn(false);
        when(roleMapper.toEntity(request)).thenReturn(Role.builder().name(request.name()).build());
        when(permissionRepository.findAllById(request.permissionIds())).thenReturn(List.of());

        assertThatThrownBy(() -> roleService.createRole(request))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Permissions not found");
    }
}
