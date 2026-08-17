package com.aqvp.platform.qualification.mapper;

import com.aqvp.platform.qualification.domain.Department;
import com.aqvp.platform.qualification.dto.DepartmentRequestDto;
import com.aqvp.platform.qualification.dto.DepartmentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for {@link Department} entity and DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DepartmentMapper {

    @Mapping(target = "faculty", ignore = true)
    Department toEntity(DepartmentRequestDto dto);

    @Mapping(target = "facultyId", source = "faculty.id")
    @Mapping(target = "facultyName", source = "faculty.name")
    @Mapping(target = "institutionId", source = "faculty.institution.id")
    @Mapping(target = "institutionName", source = "faculty.institution.name")
    DepartmentResponseDto toResponseDto(Department department);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "faculty", ignore = true)
    void updateEntity(DepartmentRequestDto dto, @MappingTarget Department department);
}
