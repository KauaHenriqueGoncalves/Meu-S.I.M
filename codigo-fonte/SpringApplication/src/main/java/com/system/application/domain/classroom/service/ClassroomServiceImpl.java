package com.system.application.domain.classroom.service;

import com.system.application.domain.classType.ClassType;
import com.system.application.domain.classType.service.ClassTypeService;
import com.system.application.domain.classroom.Classroom;
import com.system.application.domain.classroom.dto.ClassroomRequest;
import com.system.application.domain.classroom.dto.ClassroomResponse;
import com.system.application.domain.classroom.dto.ClassroomSimpleResponse;
import com.system.application.domain.classroom.dto.ClassroomViewStudentResponse;
import com.system.application.domain.classroom.repository.ClassroomRepository;
import com.system.application.domain.school.School;
import com.system.application.domain.school.service.SchoolService;
import com.system.application.domain.student.Student;
import com.system.application.domain.student.service.StudentService;
import com.system.application.domain.subject.Subject;
import com.system.application.domain.subject.dto.SubjectResponse;
import com.system.application.domain.subject.service.SubjectService;
import com.system.application.shared.dto.PageResponse;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.BusinessException;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClassroomServiceImpl implements ClassroomService {
    private final SchoolService schoolService;
    private final ClassTypeService classTypeService;
    private final SubjectService subjectService;
    private final StudentService studentService;
    private final ClassroomRepository classroomRepository;

    public ClassroomServiceImpl(SchoolService schoolService,
                                ClassTypeService classTypeService,
                                SubjectService subjectService,
                                StudentService studentService,
                                ClassroomRepository classroomRepository) {
        this.schoolService = schoolService;
        this.classTypeService = classTypeService;
        this.subjectService = subjectService;
        this.studentService = studentService;
        this.classroomRepository = classroomRepository;
    }

    @Override
    public PageResponse<ClassroomSimpleResponse> findAllSimple(UUID userId, Pageable pageable) {
        School school = schoolService.findByUser(userId);
        Page<ClassroomSimpleResponse> classes = classroomRepository.findAllBySchoolId(school.getId(), pageable)
                .map(c -> new ClassroomSimpleResponse(c.getId(), c.getClassType().getName(), c.getSubject().getName(), c.getName()));
        return new PageResponse<>(
                classes.getContent(),
                classes.getNumber(),
                classes.getSize(),
                classes.getTotalPages(),
                classes.getTotalElements(),
                classes.hasNext(),
                classes.hasPrevious()
        );
    }

    @Override
    public ClassroomResponse findById(UUID userId, UUID classroomId) {
        School school = schoolService.findByUser(userId);
        Classroom classroom = this.findByIdEntity(classroomId);
        if (!classroom.getSchool().equals(school)) {
            throw new AccessDeniedException("Não é possivel interagir com turma de outra escola");
        }
        return new ClassroomResponse(
                classroom.getId(),
                classroom.getClassType(),
                new SubjectResponse(classroom.getSubject().getId(), classroom.getSubject().getName()),
                classroom.getName(),
                classroom.getMaxStudents(),
                classroom.getStudents().stream()
                        .sorted(Comparator.comparing(Student::getName))
                        .map(c -> new ClassroomViewStudentResponse(c.getId(), c.getName()))
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
    }

    @Override
    public Classroom findByIdEntity(UUID classroomId) {
        return classroomRepository.findById(classroomId)
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou a classe"));
    }

    @Override
    @Transactional
    public UUID save(UUID userId, ClassroomRequest request) {
        School school = schoolService.findByUser(userId);
        ClassType classType = classTypeService.findById(request.classTypeId());
        boolean isIndividual = classType.getId() == ClassType.Values.INDIVIDUAL.getValue();
        if (isIndividual) {
            if (!(request.maxStudents() == 1))
                throw new IllegalArgumentException("Turmas individuais devem ter apenas 1 estudante");
        }
        Subject subject = subjectService.findByIdEntity(request.subjectId());
        Classroom classroom = new Classroom(null, school, classType, subject, request.name(), request.maxStudents(), null);
        classroom = classroomRepository.save(classroom);
        return classroom.getId();
    }

    @Override
    @Transactional
    public UUID update(UUID userId, UUID classroomId, ClassroomRequest request) {
        School school = schoolService.findByUser(userId);
        Classroom classroom = this.findByIdEntity(classroomId);
        if (!classroom.getSchool().equals(school)) {
            throw new AccessDeniedException("Não é possivel interagir com turma de outra escola");
        }
        ClassType classType = classTypeService.findById(request.classTypeId());
        boolean isIndividual = classType.getId() == ClassType.Values.INDIVIDUAL.getValue();
        if (isIndividual) {
            if (!(request.maxStudents() == 1))
                throw new IllegalArgumentException("Turmas individuais devem ter apenas 1 estudante");
        }
        Subject subject = subjectService.findByIdEntity(request.subjectId());
        classroom.setSubject(subject);
        classroom.setClassType(classType);
        classroom.setMaxStudents(request.maxStudents());
        classroom.setName(request.name());
        classroom = classroomRepository.save(classroom);
        return classroom.getId();
    }

    @Override
    @Transactional
    public void addStudent(UUID userId, UUID classroomId, UUID studentId) {
        School school = schoolService.findByUser(userId);
        Classroom classroom = this.findByIdEntity(classroomId);
        if (!classroom.getSchool().equals(school)) {
            throw new AccessDeniedException("Não é possivel interagir com turma de outra escola");
        }
        Student student = studentService.findByIdEntity(studentId);
        if (!student.getSchool().equals(school)) {
            throw new AccessDeniedException("Não é possivel interagir com estudantes de outra escola");
        }
        if (!(classroom.getStudents().size() < classroom.getMaxStudents())) {
            throw new BusinessException("Turma cheia");
        }
        classroom.getStudents().add(student);
        classroomRepository.save(classroom);
    }

    @Override
    @Transactional
    public void removeStudent(UUID userId, UUID classroomId, UUID studentId) {
        School school = schoolService.findByUser(userId);
        Classroom classroom = this.findByIdEntity(classroomId);
        if (!classroom.getSchool().equals(school)) {
            throw new AccessDeniedException("Não é possivel interagir com turma de outra escola");
        }
        Student student = studentService.findByIdEntity(studentId);
        if (!student.getSchool().equals(school)) {
            throw new AccessDeniedException("Não é possivel interagir com estudantes de outra escola");
        }
        if (!classroom.getStudents().contains(student)) {
            throw new BusinessException("Estudante não pertencer à turma");
        }
        classroom.getStudents().remove(student);
        classroomRepository.save(classroom);
    }

    @Override
    @Transactional
    public void deleteById(UUID userId, UUID classroomId) {
        School school = schoolService.findByUser(userId);
        Classroom classroom = this.findByIdEntity(classroomId);
        if (!classroom.getSchool().equals(school)) {
            throw new AccessDeniedException("Não é possivel interagir com turma de outra escola");
        }
        if (!classroom.getStudents().isEmpty()) {
            throw new BusinessException("Não é possivel apagar turma com estudantes");
        }
        classroomRepository.deleteById(classroom.getId());
    }
}
