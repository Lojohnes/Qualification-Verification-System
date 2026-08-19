package com.aqvp.platform.qualification.mapper;

import com.aqvp.platform.qualification.domain.Student;
import com.aqvp.platform.qualification.dto.StudentRequestDto;
import com.aqvp.platform.qualification.dto.StudentResponseDto;
import com.aqvp.platform.qualification.dto.StudentUpdateRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for {@link Student} entity and DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper {

    Student toEntity(StudentRequestDto dto);

    StudentResponseDto toResponseDto(Student student);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentNumber", ignore = true)
    @Mapping(target = "institutionId", ignore = true)
    void updateEntity(StudentUpdateRequestDto dto, @MappingTarget Student student);
}
