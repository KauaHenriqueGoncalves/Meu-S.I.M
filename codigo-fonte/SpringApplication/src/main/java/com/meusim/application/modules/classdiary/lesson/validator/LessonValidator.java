package com.meusim.application.modules.classdiary.lesson.validator;

import com.meusim.application.modules.academic.classroom.Classroom;
import com.meusim.application.modules.school.School;

public interface LessonValidator {
    void ensureLessonClassroomBelongsSameSchool(School ownerSchool, Classroom classroom);
}
