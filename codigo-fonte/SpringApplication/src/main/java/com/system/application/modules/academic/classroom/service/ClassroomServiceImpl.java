package com.system.application.modules.academic.classroom.service;

import com.system.application.modules.academic.classtype.ClassType;
import com.system.application.modules.academic.classtype.service.ClassTypeService;
import com.system.application.modules.academic.classroom.Classroom;
import com.system.application.modules.academic.classroom.dto.ClassroomRequest;
import com.system.application.modules.academic.classroom.dto.ClassroomDetailResponse;
import com.system.application.modules.academic.classroom.dto.ClassroomResponse;
import com.system.application.modules.academic.classroom.dto.ClassroomViewStudentResponse;
import com.system.application.modules.academic.classroom.repository.ClassroomRepository;
import com.system.application.modules.school.School;
import com.system.application.modules.school.service.SchoolService;
import com.system.application.modules.academic.student.Student;
import com.system.application.modules.academic.student.service.StudentService;
import com.system.application.modules.academic.subject.Subject;
import com.system.application.modules.academic.subject.dto.SubjectResponse;
import com.system.application.modules.academic.subject.service.SubjectService;
import com.system.application.shared.dto.PageResponse;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.BusinessException;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClassroomServiceImpl implements ClassroomService {
    private final ClassroomRepository classroomRepository;
    private final SchoolService schoolService;
    private final ClassTypeService classTypeService;
    private final SubjectService subjectService;
    private final StudentService studentService;

    public ClassroomServiceImpl(
            ClassroomRepository classroomRepository,
            SchoolService schoolService,
            ClassTypeService classTypeService,
            SubjectService subjectService,
            StudentService studentService
    ) {
        this.classroomRepository = classroomRepository;
        this.schoolService = schoolService;
        this.classTypeService = classTypeService;
        this.subjectService = subjectService;
        this.studentService = studentService;
    }

    @Override
    public PageResponse<ClassroomResponse> findAllResponseBySchool(UUID userId, int page, int size) {
        School school = schoolService.findByUserId(userId);
        Pageable pageable = PageRequest.of(page, size);
        Page<ClassroomResponse> response = classroomRepository.findAllBySchoolId(school.getId(), pageable)
                .map(c ->
                    new ClassroomResponse(
                            c.getId(),
                            c.getClassType().getName(),
                            c.getSubject().getName(),
                            c.getName()
                    )
                );
        return PageResponse.from(response);
    }

    @Override
    public List<ClassroomResponse> findAllResponseByStudentId(UUID userId, UUID studentId) {
        School school = schoolService.findByUserId(userId);
        Student student = studentService.findById(studentId);
        ensureStudentBelongsToSchool(school.getId(), student);
        return classroomRepository.findAllResponseByStudentId(student.getId())
                .stream().map(c ->
                        new ClassroomResponse(
                                c.getId(),
                                c.getClassType().getName(),
                                c.getSubject().getName(),
                                c.getName())
                ).toList();
    }

    @Override
    public ClassroomDetailResponse findDetailResponseById(UUID userId, UUID classroomId) {
        School school = schoolService.findByUserId(userId);
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou a classe"));
        ensureClassroomBelongsToSchool(school.getId(), classroom);
        return new ClassroomDetailResponse(
                classroom.getId(),
                classroom.getClassType(),
                new SubjectResponse(
                        classroom.getSubject().getId(),
                        classroom.getSubject().getName()
                ),
                classroom.getName(),
                classroom.getMaxStudents(),
                classroom.getStudents().stream()
                        .sorted(Comparator.comparing(Student::getName))
                        .map(c ->
                                new ClassroomViewStudentResponse(c.getId(), c.getName()))
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
    }

    @Override
    public Classroom findById(UUID classroomId) {
        return classroomRepository.findById(classroomId)
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou a classe"));
    }

    @Override
    @Transactional
    public Classroom save(UUID userId, ClassroomRequest request) {
        School school = schoolService.findByUserId(userId);
        ClassType classType = classTypeService.findById(request.classTypeId());
        ensureIsIndividual(classType, request);
        Subject subject = subjectService.findById(request.subjectId());
        ensureSubjectBelongsToSchool(school.getId(), subject);
        Classroom classroom =
                new Classroom(null, school, classType, subject, request.name(), request.maxStudents(), null);
        classroom = classroomRepository.save(classroom);
        return classroom;
    }

    @Override
    @Transactional
    public void update(UUID userId, UUID classroomId, ClassroomRequest request) {
        School school = schoolService.findByUserId(userId);
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou a classe"));
        ensureClassroomBelongsToSchool(school.getId(), classroom);
        ClassType classType = classTypeService.findById(request.classTypeId());
        ensureIsIndividual(classType, request);
        ensureStudentCountIsBelowMax(classroom, request);
        Subject subject = subjectService.findById(request.subjectId());
        classroom.setSubject(subject);
        classroom.setClassType(classType);
        classroom.setMaxStudents(request.maxStudents());
        classroom.setName(request.name());
        classroomRepository.save(classroom);
    }

    @Override
    @Transactional
    public void addStudent(UUID userId, UUID classroomId, UUID studentId) {
        School school = schoolService.findByUserId(userId);
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou a classe"));
        ensureClassroomBelongsToSchool(school.getId(), classroom);
        Student student = studentService.findById(studentId);
        ensureStudentBelongsToSchool(school.getId(), student);
        ensureClassroomFull(classroom);
        classroom.getStudents().add(student);
        classroomRepository.save(classroom);
    }

    @Override
    @Transactional
    public void removeStudent(UUID userId, UUID classroomId, UUID studentId) {
        School school = schoolService.findByUserId(userId);
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou a classe"));
        ensureClassroomBelongsToSchool(school.getId(), classroom);
        Student student = studentService.findById(studentId);
        ensureStudentBelongsToSchool(school.getId(), student);
        ensureClassroomContainsStudent(classroom, student);
        classroom.getStudents().remove(student);
        classroomRepository.save(classroom);
    }

    @Override
    @Transactional
    public void deleteById(UUID userId, UUID classroomId) {
        School school = schoolService.findByUserId(userId);
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou a classe"));
        ensureClassroomBelongsToSchool(school.getId(), classroom);
        if (!classroom.getStudents().isEmpty()) {
            throw new BusinessException("Não é possivel apagar turma com estudantes");
        }
        classroomRepository.deleteById(classroom.getId());
    }

    private void ensureIsIndividual(ClassType classType, ClassroomRequest request) {
        boolean isIndividual = classType.getId() == ClassType.Values.INDIVIDUAL.getValue();
        if (isIndividual) {
            if (!(request.maxStudents() == 1))
                throw new IllegalArgumentException("Turmas individuais devem ter apenas 1 estudante");
        }
    }

    private void ensureClassroomFull(Classroom classroom) {
        if (!(classroom.getStudents().size() < classroom.getMaxStudents())) {
            throw new BusinessException("Turma cheia");
        }
    }

    private void ensureStudentCountIsBelowMax(Classroom classroom, ClassroomRequest request) {
        if (!(classroom.getStudents().size() < request.maxStudents())) {
            throw new IllegalArgumentException("A turma possui quantidade acima do informado");
        }
    }

    private void ensureClassroomContainsStudent(Classroom classroom, Student student) {
        if (!classroom.getStudents().contains(student)) {
            throw new BusinessException("Estudante não pertencer à turma");
        }
    }

    private void ensureSubjectBelongsToSchool(UUID schoolId, Subject subject) {
        if (!subject.getSchool().getId().equals(schoolId)) {
            throw new AccessDeniedException("Não é possivel interagir com disciplina de outra escola");
        }
    }

    private void ensureClassroomBelongsToSchool(UUID schoolId, Classroom classroom) {
        if (!classroom.getSchool().getId().equals(schoolId)) {
            throw new AccessDeniedException("Não é possivel interagir com turma de outra escola");
        }
    }

    private void ensureStudentBelongsToSchool(UUID schoolId, Student student) {
        if (!student.getSchool().getId().equals(schoolId)) {
            throw new AccessDeniedException("Não é possivel interagir com estudantes de outra escola");
        }
    }
}
