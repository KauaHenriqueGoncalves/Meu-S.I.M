package com.system.application.core.classschedule.service;

import com.system.application.core.classschedule.ClassSchedule;
import com.system.application.core.classschedule.dto.ClassScheduleRequest;
import com.system.application.core.classschedule.dto.ClassScheduleResponse;

import java.util.List;
import java.util.UUID;

public interface ClassScheduleService {
    List<ClassScheduleResponse> findAllResponseByClassroom(UUID userId, UUID classroomId);
    ClassSchedule findById(UUID classScheduleId);
    ClassSchedule save(UUID userId, UUID classroomId, ClassScheduleRequest request);
    void update(UUID userId, UUID classroomId, UUID classScheduleId, ClassScheduleRequest updateRequest);
    void deleteById(UUID userId, UUID classroomId, UUID classScheduleId);
}
