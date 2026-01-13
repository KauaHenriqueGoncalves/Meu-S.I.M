package com.system.application.shared.mapper;

public interface BaseMapper <ENTITY, DTO> {
    ENTITY toEntity(DTO dto);
    DTO toDto(ENTITY entity);
}
