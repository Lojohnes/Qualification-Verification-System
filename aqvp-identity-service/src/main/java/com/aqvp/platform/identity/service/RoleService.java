package com.aqvp.platform.identity.service;

import com.aqvp.platform.identity.dto.RoleRequestDto;
import com.aqvp.platform.identity.dto.RoleResponseDto;
import java.util.List;
import java.util.UUID;

/**
 * Service contract for managing roles and their permission mappings.
 */
public interface RoleService {

    RoleResponseDto createRole(RoleRequestDto dto);

    RoleResponseDto updateRole(UUID id, RoleRequestDto dto);

    RoleResponseDto findById(UUID id);

    List<RoleResponseDto> findAll();
}
