package com.system.application.domain.school.mapper;

import com.system.application.domain.school.School;
import com.system.application.domain.school.dto.SchoolRequest;
import org.springframework.stereotype.Component;

@Component
public class SchoolMapperImpl implements SchoolMapper {
    public SchoolMapperImpl() {}

    @Override
    public School toEntity(SchoolRequest schoolRequest) {
        return new School(
                null,
                schoolRequest.nameCode(),
                schoolRequest.schoolName(),
                schoolRequest.cnpj()
        );
    }
}
