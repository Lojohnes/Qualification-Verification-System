package com.aqvp.platform.admin.mapper;

import com.aqvp.platform.admin.domain.AuditEvent;
import com.aqvp.platform.admin.dto.AuditEventRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AuditEventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "occurredAt", expression = "java(java.time.LocalDateTime.now())")
    AuditEvent toEntity(AuditEventRequestDto dto);
}
