package com.system.application.domain.student.mapper;

import com.system.application.domain.legalGuardian.dto.LegalGuardianResponse;
import com.system.application.domain.student.Student;
import com.system.application.domain.student.dto.StudentResponse;
import org.springframework.stereotype.Component;

@Component
public class StudentMapperImpl implements StudentMapper {
    public StudentMapperImpl() {}

    @Override
    public StudentResponse toDto(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getName(),
                student.getDateOfBirth(),
                student.getGrade(),
                new LegalGuardianResponse(
                        student.getLegalGuardian().getId(),
                        student.getLegalGuardian().getUser().getUsername(),
                        student.getLegalGuardian().getDegreeOfKinship()
                )
        );
    }
}
