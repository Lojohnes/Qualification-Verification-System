package com.aqvp.platform.identity.service;

import com.aqvp.platform.identity.dto.PermissionDto;
import java.util.List;
import java.util.UUID;

/**
 * Service contract for querying permissions.
 */
public interface PermissionService {

    PermissionDto findById(UUID id);

    List<PermissionDto> findAll();
}
