package com.aqvp.platform.identity.mapper;

import com.aqvp.platform.identity.domain.ApiClient;
import com.aqvp.platform.identity.dto.ApiClientRequestDto;
import com.aqvp.platform.identity.dto.ApiClientResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for {@link ApiClient} entity and DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ApiClientMapper {

    @Mapping(target = "clientSecretHash", ignore = true)
    ApiClient toEntity(ApiClientRequestDto dto);

    ApiClientResponseDto toResponseDto(ApiClient client);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clientSecretHash", ignore = true)
    void updateEntity(ApiClientRequestDto dto, @MappingTarget ApiClient client);
}
