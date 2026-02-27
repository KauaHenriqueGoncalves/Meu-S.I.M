package com.system.application.core.classschedule.repository;

import com.system.application.core.classschedule.ClassSchedule;
import com.system.application.core.classschedule.dto.ClassScheduleResponse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassScheduleRepository extends CrudRepository<ClassSchedule, UUID> {
    Optional<List<ClassSchedule>> findByClassroomId(UUID classroomId);
}
