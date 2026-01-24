package com.system.application.domain.student.mapper;

import com.system.application.domain.student.Student;
import com.system.application.domain.student.dto.StudentResponse;

public interface StudentMapper {
    StudentResponse toDto(Student student);
}
