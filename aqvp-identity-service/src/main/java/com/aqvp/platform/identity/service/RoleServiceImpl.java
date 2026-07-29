package com.aqvp.platform.identity.service;

import com.aqvp.platform.identity.domain.Permission;
import com.aqvp.platform.identity.domain.Role;
import com.aqvp.platform.identity.dto.RoleRequestDto;
import com.aqvp.platform.identity.dto.RoleResponseDto;
import com.aqvp.platform.identity.exception.EntityNotFoundException;
import com.aqvp.platform.identity.mapper.RoleMapper;
import com.aqvp.platform.identity.repository.PermissionRepository;
import com.aqvp.platform.identity.repository.RoleRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements role management and permission assignment.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('role:write')")
    public RoleResponseDto createRole(RoleRequestDto dto) {
        if (roleRepository.existsByName(dto.name())) {
            throw new IllegalArgumentException("Role already exists: " + dto.name());
        }
        final Role role = roleMapper.toEntity(dto);
        role.setPermissions(resolvePermissions(dto.permissionIds()));
        final Role saved = roleRepository.save(role);
        log.info("Created role '{}' with id {}", saved.getName(), saved.getId());
        return roleMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('role:write')")
    public RoleResponseDto updateRole(UUID id, RoleRequestDto dto) {
        final Role role = roleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Role not found: " + id));
        roleMapper.updateEntity(dto, role);
        role.setPermissions(resolvePermissions(dto.permissionIds()));
        final Role saved = roleRepository.save(role);
        log.info("Updated role '{}' with id {}", saved.getName(), saved.getId());
        return roleMapper.toResponseDto(saved);
    }

    @Override
    @PreAuthorize("hasAuthority('role:read')")
    public RoleResponseDto findById(UUID id) {
        return roleRepository.findById(id)
            .map(roleMapper::toResponseDto)
            .orElseThrow(() -> new EntityNotFoundException("Role not found: " + id));
    }

    @Override
    @PreAuthorize("hasAuthority('role:read')")
    public List<RoleResponseDto> findAll() {
        return roleRepository.findAll().stream()
            .map(roleMapper::toResponseDto)
            .toList();
    }

    private Set<Permission> resolvePermissions(Set<UUID> permissionIds) {
        final Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
        if (permissions.size() != permissionIds.size()) {
            final Set<UUID> missing = permissionIds.stream()
                .filter(id -> permissions.stream().noneMatch(permission -> permission.getId().equals(id)))
                .collect(Collectors.toSet());
            throw new EntityNotFoundException("Permissions not found: " + missing);
        }
        return permissions;
    }
}
