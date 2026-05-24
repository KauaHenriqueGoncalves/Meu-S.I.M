package com.system.application.modules.academic.classroom.dto;

import com.system.application.modules.academic.student.Student;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record ClassroomViewStudentResponse(

        UUID id,
        String name

) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static ClassroomViewStudentResponse of(Student s) {
        return new ClassroomViewStudentResponse(
                s.getId(),
                s.getName()
        );
    }
}
