package com.system.application.domain.school.mapper;

import com.system.application.domain.school.School;
import com.system.application.domain.school.dto.SchoolRequest;

public interface SchoolMapper {
    School toEntity(SchoolRequest schoolRequest);
}
