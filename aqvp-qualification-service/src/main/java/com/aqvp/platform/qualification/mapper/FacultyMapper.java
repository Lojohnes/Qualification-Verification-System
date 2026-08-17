package com.aqvp.platform.qualification.mapper;

import com.aqvp.platform.qualification.domain.Faculty;
import com.aqvp.platform.qualification.dto.FacultyRequestDto;
import com.aqvp.platform.qualification.dto.FacultyResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for {@link Faculty} entity and DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FacultyMapper {

    @Mapping(target = "institution", ignore = true)
    Faculty toEntity(FacultyRequestDto dto);

    @Mapping(target = "institutionId", source = "institution.id")
    @Mapping(target = "institutionName", source = "institution.name")
    FacultyResponseDto toResponseDto(Faculty faculty);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "institution", ignore = true)
    void updateEntity(FacultyRequestDto dto, @MappingTarget Faculty faculty);
}
