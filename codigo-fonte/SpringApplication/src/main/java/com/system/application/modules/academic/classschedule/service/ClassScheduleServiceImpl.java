package com.system.application.modules.academic.classschedule.service;

import com.system.application.modules.academic.classschedule.ClassSchedule;
import com.system.application.modules.academic.classschedule.dto.ClassScheduleRequest;
import com.system.application.modules.academic.classschedule.dto.ClassScheduleResponse;
import com.system.application.modules.academic.classschedule.repository.ClassScheduleRepository;
import com.system.application.modules.academic.classroom.Classroom;
import com.system.application.modules.academic.classroom.service.ClassroomService;
import com.system.application.modules.school.School;
import com.system.application.modules.school.service.SchoolService;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ClassScheduleServiceImpl implements ClassScheduleService {
    private final ClassScheduleRepository classScheduleRepository;
    private final SchoolService schoolService;
    private final ClassroomService classroomService;

    public ClassScheduleServiceImpl(
            ClassScheduleRepository classScheduleRepository,
            SchoolService schoolService,
            ClassroomService classroomService
    ) {
        this.classScheduleRepository = classScheduleRepository;
        this.schoolService = schoolService;
        this.classroomService = classroomService;
    }

    @Override
    public List<ClassScheduleResponse> findAllResponseByClassroom(UUID userId, UUID classroomId) {
        School school = schoolService.findByUserId(userId);
        Classroom classroom = classroomService.findById(classroomId);
        ensureClassroomBelongsSchool(school.getId(), classroom);
        List<ClassSchedule> response =
                classScheduleRepository.findByClassroomId(classroom.getId()).get();
        return response.stream()
                .sorted(Comparator
                        .comparing((ClassSchedule c) -> c.getWeekday().getOrder())
                        .thenComparing(ClassSchedule::getStartTime)
                )
                .map(c -> new ClassScheduleResponse(
                        c.getId(),
                        c.getWeekday().getDescription(),
                        c.getStartTime(),
                        c.getEndTime()
                )).toList();
    }

    @Override
    public ClassSchedule findById(UUID classScheduleId) {
        return classScheduleRepository.findById(classScheduleId)
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou o horário da turma"));
    }

    @Override
    @Transactional
    public ClassSchedule save(UUID userId, UUID classroomId, ClassScheduleRequest request) {
        School school = schoolService.findByUserId(userId);
        Classroom classroom = classroomService.findById(classroomId);
        ensureClassroomBelongsSchool(school.getId(), classroom);
        ClassSchedule classSchedule = new ClassSchedule(
                null,
                classroom,
                request.weekday(),
                request.startTime(),
                request.endTime()
        );
        classSchedule = classScheduleRepository.save(classSchedule);
        return classSchedule;
    }

    @Override
    @Transactional
    public void update(UUID userId, UUID classroomId, UUID classScheduleId, ClassScheduleRequest updateRequest) {
        School school = schoolService.findByUserId(userId);
        ClassSchedule schedule = findById(classScheduleId);
        ensureClassScheduleBelongsSchool(school.getId(), schedule);
        ensureClassScheduleBelongsClassroom(classroomId, schedule);
        schedule.setWeekday(updateRequest.weekday());
        schedule.setStartTime(updateRequest.startTime());
        schedule.setEndTime(updateRequest.endTime());
    }

    @Override
    @Transactional
    public void deleteById(UUID userId, UUID classroomId, UUID classScheduleId) {
        School school = schoolService.findByUserId(userId);
        ClassSchedule schedule = findById(classScheduleId);
        ensureClassScheduleBelongsSchool(school.getId(), schedule);
        ensureClassScheduleBelongsClassroom(classroomId, schedule);
        classScheduleRepository.deleteById(schedule.getId());
    }

    private void ensureClassroomBelongsSchool(UUID schoolId, Classroom classroom) {
        if (!classroom.getSchool().getId().equals(schoolId)) {
            throw new AccessDeniedException("Não é possivel interagir com turma de outra escola");
        }
    }

    private void ensureClassScheduleBelongsSchool(UUID schoolId, ClassSchedule classSchedule) {
        if (!classSchedule.getClassroom().getSchool().getId().equals(schoolId)) {
            throw new AccessDeniedException("Não é possivel interagir com turma de outra escola");
        }
    }

    private void ensureClassScheduleBelongsClassroom(UUID classroomId, ClassSchedule classSchedule) {
        if (!classSchedule.getClassroom().getId().equals(classroomId)) {
            throw new AccessDeniedException("Não é possivel interagir com o horário de outra turma");
        }
    }
}
