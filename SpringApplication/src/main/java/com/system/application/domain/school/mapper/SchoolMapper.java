package com.system.application.domain.school.mapper;

import com.system.application.domain.school.School;
import com.system.application.domain.school.dto.SchoolRequest;
import com.system.application.shared.mapper.BaseMapper;
import org.springframework.stereotype.Component;

@Component
public class SchoolMapper implements BaseMapper<School, SchoolRequest> {
    public SchoolMapper() {}

    @Override
    public School toEntity(SchoolRequest schoolRequest) {
        return new School(
                null,
                schoolRequest.nameCode(),
                schoolRequest.schoolName(),
                schoolRequest.cnpj()
        );
    }

    @Override
    public SchoolRequest toDto(School school) {
        return null;
    }
}
