package com.meusim.application.modules.academic.student.service;

import com.meusim.application.modules.academic.student.dto.StudentResponse;

import java.util.List;
import java.util.UUID;

public interface StudentQuery {
    List<StudentResponse> findAllStudentByLegalGuardianId(UUID id);
}
