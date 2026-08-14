package com.aqvp.platform.qualification.mapper;

import com.aqvp.platform.qualification.domain.Institution;
import com.aqvp.platform.qualification.dto.InstitutionRequestDto;
import com.aqvp.platform.qualification.dto.InstitutionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for {@link Institution} entity and DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InstitutionMapper {

    @Mapping(target = "faculties", ignore = true)
    @Mapping(target = "programs", ignore = true)
    Institution toEntity(InstitutionRequestDto dto);

    InstitutionResponseDto toResponseDto(Institution institution);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "faculties", ignore = true)
    @Mapping(target = "programs", ignore = true)
    void updateEntity(InstitutionRequestDto dto, @MappingTarget Institution institution);
}
