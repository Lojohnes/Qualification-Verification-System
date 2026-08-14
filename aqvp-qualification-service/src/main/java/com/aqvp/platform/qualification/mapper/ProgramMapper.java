package com.aqvp.platform.qualification.mapper;

import com.aqvp.platform.qualification.domain.Program;
import com.aqvp.platform.qualification.dto.ProgramRequestDto;
import com.aqvp.platform.qualification.dto.ProgramResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for {@link Program} entity and DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProgramMapper {

    @Mapping(target = "institution", ignore = true)
    @Mapping(target = "department", ignore = true)
    Program toEntity(ProgramRequestDto dto);

    @Mapping(target = "institutionId", source = "institution.id")
    @Mapping(target = "institutionName", source = "institution.name")
    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    ProgramResponseDto toResponseDto(Program program);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "institution", ignore = true)
    @Mapping(target = "department", ignore = true)
    void updateEntity(ProgramRequestDto dto, @MappingTarget Program program);
}
