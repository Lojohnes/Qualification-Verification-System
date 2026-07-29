package com.aqvp.platform.identity.mapper;

import com.aqvp.platform.identity.domain.User;
import com.aqvp.platform.identity.dto.UserRequestDto;
import com.aqvp.platform.identity.dto.UserResponseDto;
import com.aqvp.platform.identity.dto.UserUpdateRequestDto;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for {@link User} entity and DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "refreshTokens", ignore = true)
    User toEntity(UserRequestDto dto);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "roleNames")
    UserResponseDto toResponseDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "refreshTokens", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "mfaEnabled", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    void updateEntity(UserUpdateRequestDto dto, @MappingTarget User user);

    @Named("roleNames")
    default java.util.Set<String> roleNames(java.util.Set<com.aqvp.platform.identity.domain.Role> roles) {
        if (roles == null) {
            return java.util.Collections.emptySet();
        }
        return roles.stream()
            .map(com.aqvp.platform.identity.domain.Role::getName)
            .collect(Collectors.toSet());
    }
}
