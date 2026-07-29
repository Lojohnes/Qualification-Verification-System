package com.aqvp.platform.identity.mapper;

import com.aqvp.platform.identity.domain.Permission;
import com.aqvp.platform.identity.dto.PermissionDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for {@link Permission} entity and DTO.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionMapper {

    PermissionDto toDto(Permission permission);
}
