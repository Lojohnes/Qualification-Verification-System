package com.aqvp.platform.identity.mapper;

import com.aqvp.platform.identity.domain.Permission;
import com.aqvp.platform.identity.domain.Role;
import com.aqvp.platform.identity.dto.RoleRequestDto;
import com.aqvp.platform.identity.dto.RoleResponseDto;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for {@link Role} entity and DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    Role toEntity(RoleRequestDto dto);

    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "permissionNames")
    RoleResponseDto toResponseDto(Role role);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    void updateEntity(RoleRequestDto dto, @MappingTarget Role role);

    @Named("permissionNames")
    default Set<String> permissionNames(Set<Permission> permissions) {
        if (permissions == null) {
            return java.util.Collections.emptySet();
        }
        return permissions.stream()
            .map(Permission::getName)
            .collect(Collectors.toSet());
    }
}
