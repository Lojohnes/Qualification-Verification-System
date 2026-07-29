package com.aqvp.platform.identity.service;

import com.aqvp.platform.identity.dto.PermissionDto;
import com.aqvp.platform.identity.exception.EntityNotFoundException;
import com.aqvp.platform.identity.mapper.PermissionMapper;
import com.aqvp.platform.identity.repository.PermissionRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements permission query operations.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    @PreAuthorize("hasAuthority('role:read')")
    public PermissionDto findById(UUID id) {
        return permissionRepository.findById(id)
            .map(permissionMapper::toDto)
            .orElseThrow(() -> new EntityNotFoundException("Permission not found: " + id));
    }

    @Override
    @PreAuthorize("hasAuthority('role:read')")
    public List<PermissionDto> findAll() {
        return permissionRepository.findAllByOrderByNameAsc().stream()
            .map(permissionMapper::toDto)
            .toList();
    }
}
