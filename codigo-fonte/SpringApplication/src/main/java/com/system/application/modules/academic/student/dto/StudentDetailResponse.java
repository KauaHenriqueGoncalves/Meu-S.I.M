package com.system.application.modules.academic.student.dto;

import com.system.application.modules.academic.student.Student;
import com.system.application.modules.identity.legalguardian.dto.LegalGuardianResponse;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public record StudentDetailResponse(

        UUID id,
        String name,
        LocalDate dateOfBirth,
        String grade,
        LegalGuardianResponse legalGuardianResponse

) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static StudentDetailResponse of(Student s) {
        return new StudentDetailResponse(
                s.getId(),
                s.getName(),
                s.getDateOfBirth(),
                s.getGrade(),
                new LegalGuardianResponse(
                        s.getLegalGuardian().getId(),
                        s.getLegalGuardian().getUser().getUsername(),
                        s.getLegalGuardian().getDegreeOfKinship()
                ));
    }
}
