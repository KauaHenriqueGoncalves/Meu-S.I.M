package com.meusim.application.modules.academic.classschedule.service;

import com.meusim.application.modules.academic.classschedule.ClassSchedule;
import com.meusim.application.modules.academic.classschedule.dto.ClassScheduleRequest;
import com.meusim.application.modules.academic.classschedule.dto.ClassScheduleResponse;
import java.util.List;
import java.util.UUID;

public interface ClassScheduleService {
    List<ClassSchedule> findAllByClassroomId(UUID classroomId);
    List<ClassScheduleResponse> findAllResponseByClassroom(UUID userId, UUID classroomId);
    ClassSchedule findById(UUID classScheduleId);
    ClassSchedule save(UUID userId, UUID classroomId, ClassScheduleRequest request);
    void update(UUID userId, UUID classroomId, UUID classScheduleId, ClassScheduleRequest updateRequest);
    void deleteById(UUID userId, UUID classroomId, UUID classScheduleId);
}
