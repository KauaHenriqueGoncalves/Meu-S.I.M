package com.meusim.application.modules.classdiary.lesson.validator;

import com.meusim.application.modules.academic.classroom.Classroom;
import com.meusim.application.modules.classdiary.lesson.repository.LessonRepository;
import com.meusim.application.modules.school.School;
import com.meusim.application.shared.exception.AccessDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LessonValidatorImpl implements LessonValidator {
    private final static Logger log = LoggerFactory.getLogger(LessonValidatorImpl.class);
    private final LessonRepository repository;

    public LessonValidatorImpl(LessonRepository repository) {
        this.repository = repository;
    }

    @Override
    public void ensureLessonClassroomBelongsSameSchool(School ownerSchool, Classroom classroom) {
        if (classroom.getSchool().getId().equals(ownerSchool.getId())) {
            log.warn("A agenda não pertecem o mesmo reforco que o requisitante. [ownerSchoolId={}] [classroomSchoolId={}] [classroomId={}]",
                    ownerSchool.getId(), classroom.getSchool().getId(), classroom.getId());
            throw new AccessDeniedException("A agenda não pertecem ao reforço");
        }
    }
}
