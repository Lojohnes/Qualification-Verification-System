package com.aqvp.platform.qualification.mapper;

import com.aqvp.platform.qualification.domain.Qualification;
import com.aqvp.platform.qualification.domain.QualificationStatusHistory;
import com.aqvp.platform.qualification.dto.QualificationRequestDto;
import com.aqvp.platform.qualification.dto.QualificationResponseDto;
import com.aqvp.platform.qualification.dto.QualificationStatusHistoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for {@link Qualification} entity and DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QualificationMapper {

    @Mapping(target = "qualificationType", expression = "java(com.aqvp.platform.qualification.domain.QualificationType.valueOf(dto.qualificationType()))")
    @Mapping(target = "statusHistory", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "securityIdentifier", ignore = true)
    @Mapping(target = "issuedAt", ignore = true)
    @Mapping(target = "revokedAt", ignore = true)
    @Mapping(target = "revocationReason", ignore = true)
    Qualification toEntity(QualificationRequestDto dto);

    @Mapping(target = "qualificationType", expression = "java(qualification.getQualificationType().name())")
    @Mapping(target = "status", expression = "java(qualification.getStatus().name())")
    QualificationResponseDto toResponseDto(Qualification qualification);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "qualificationNumber", ignore = true)
    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "institutionId", ignore = true)
    @Mapping(target = "qualificationType", expression = "java(com.aqvp.platform.qualification.domain.QualificationType.valueOf(dto.qualificationType()))")
    @Mapping(target = "statusHistory", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "securityIdentifier", ignore = true)
    @Mapping(target = "issuedAt", ignore = true)
    @Mapping(target = "revokedAt", ignore = true)
    @Mapping(target = "revocationReason", ignore = true)
    void updateEntity(QualificationRequestDto dto, @MappingTarget Qualification qualification);

    QualificationStatusHistoryDto toHistoryDto(QualificationStatusHistory history);
}
