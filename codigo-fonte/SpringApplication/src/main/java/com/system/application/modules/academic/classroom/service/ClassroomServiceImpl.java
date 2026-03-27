package com.system.application.modules.academic.classroom.service;

import com.system.application.modules.academic.classtype.ClassType;
import com.system.application.modules.academic.classtype.service.ClassTypeService;
import com.system.application.modules.academic.classroom.Classroom;
import com.system.application.modules.academic.classroom.dto.ClassroomRequest;
import com.system.application.modules.academic.classroom.dto.ClassroomDetailResponse;
import com.system.application.modules.academic.classroom.dto.ClassroomResponse;
import com.system.application.modules.academic.classroom.dto.ClassroomViewStudentResponse;
import com.system.application.modules.academic.classroom.repository.ClassroomRepository;
import com.system.application.modules.licensing.schoolsubscription.service.SchoolSubscriptionService;
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
import com.system.application.shared.exception.SubscriptionException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log =
            LoggerFactory.getLogger(ClassroomServiceImpl.class);

    private final ClassroomRepository classroomRepository;
    private final SchoolSubscriptionService schoolSubscriptionService;
    private final SchoolService schoolService;
    private final ClassTypeService classTypeService;
    private final SubjectService subjectService;
    private final StudentService studentService;

    public ClassroomServiceImpl(
            ClassroomRepository classroomRepository,
            SchoolSubscriptionService schoolSubscriptionService,
            SchoolService schoolService,
            ClassTypeService classTypeService,
            SubjectService subjectService,
            StudentService studentService
    ) {
        this.classroomRepository = classroomRepository;
        this.schoolSubscriptionService = schoolSubscriptionService;
        this.schoolService = schoolService;
        this.classTypeService = classTypeService;
        this.subjectService = subjectService;
        this.studentService = studentService;
    }

    @Override
    public PageResponse<ClassroomResponse> findAllResponseBySchool(UUID userId, int page, int size) {
        School school = schoolService.findByUserId(userId);

        log.info("Buscando turmas da escola. [requisitanteId={}] [schoolId={}] [page={}] [size={}]",
                userId, school.getId(), page, size);

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

        log.info("Turmas encontradas. [schoolId={}] [total={}] [totalPages={}]",
                school.getId(), response.getTotalElements(), response.getTotalPages());

        return PageResponse.from(response);
    }

    @Override
    public List<ClassroomResponse> findAllResponseByStudentId(UUID userId, UUID studentId) {
        School school = schoolService.findByUserId(userId);

        log.info("Buscando turmas do estudante. [requisitanteId={}] [studentId={}] [schoolId={}]",
                userId, studentId, school.getId());

        Student student = studentService.findById(studentId);
        ensureStudentBelongsToSchool(school.getId(), student);

        List<ClassroomResponse> response = classroomRepository
                .findAllResponseByStudentId(student.getId())
                .stream().map(c ->
                        new ClassroomResponse(
                                c.getId(),
                                c.getClassType().getName(),
                                c.getSubject().getName(),
                                c.getName())
                ).toList();

        log.info("Turmas do estudante encontradas. [studentId={}] [total={}]",
                studentId, response.size());

        return response;
    }

    @Override
    public Classroom findById(UUID classroomId) {
        return classroomRepository.findById(classroomId)
                .orElseThrow(() -> {
                    log.warn("Turma não encontrada. [classroomId={}]", classroomId);
                    return new NotFoundObjectException("Não encontrou a turma");
                });
    }

    @Override
    public ClassroomDetailResponse findDetailResponseById(UUID userId, UUID classroomId) {
        School school = schoolService.findByUserId(userId);

        log.info("Buscando detalhes da turma. [requisitanteId={}] [classroomId={}] [schoolId={}]",
                userId, classroomId, school.getId());

        Classroom classroom = findById(classroomId);
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
    @Transactional
    public Classroom save(UUID userId, ClassroomRequest request) {
        School school = schoolService.findByUserId(userId);

        log.info("Iniciando cadastro de turma. [requisitanteId={}] [schoolId={}] [nome={}] [classTypeId={}] [subjectId={}]",
                userId, school.getId(), request.name(), request.classTypeId(), request.subjectId());

        ensureSchoolHasActiveSubscription(school.getId());

        ClassType classType = classTypeService.findById(request.classTypeId());
        ensureIsIndividual(classType, request);

        Subject subject = subjectService.findById(request.subjectId());
        ensureSubjectBelongsToSchool(school.getId(), subject);

        Classroom classroom =
                new Classroom(null, school, classType, subject, request.name(), request.maxStudents(), null);
        classroom = classroomRepository.save(classroom);

        log.info("Turma cadastrada com sucesso. [classroomId={}] [schoolId={}] [tipo={}] [maxAlunos={}]",
                classroom.getId(), school.getId(), classType.getName(), classroom.getMaxStudents());

        return classroom;
    }

    @Override
    @Transactional
    public void update(UUID userId, UUID classroomId, ClassroomRequest request) {
        log.info("Iniciando atualização de turma. [requisitanteId={}] [classroomId={}]",
                userId, classroomId);

        School school = schoolService.findByUserId(userId);
        ensureSchoolHasActiveSubscription(school.getId());

        Classroom classroom = findById(classroomId);
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

        log.info("Turma atualizada com sucesso. [classroomId={}] [schoolId={}]",
                classroomId, school.getId());
    }

    @Override
    @Transactional
    public void addStudent(UUID userId, UUID classroomId, UUID studentId) {
        log.info("Adicionando estudante à turma. [requisitanteId={}] [classroomId={}] [studentId={}]",
                userId, classroomId, studentId);

        School school = schoolService.findByUserId(userId);
        ensureSchoolHasActiveSubscription(school.getId());

        Classroom classroom = findById(classroomId);
        ensureClassroomBelongsToSchool(school.getId(), classroom);

        Student student = studentService.findById(studentId);
        ensureStudentBelongsToSchool(school.getId(), student);
        ensureClassroomFull(classroom);

        classroom.getStudents().add(student);
        classroomRepository.save(classroom);

        log.info("Estudante adicionado à turma com sucesso. [classroomId={}] [studentId={}] [ocupacao={}/{}]",
                classroomId, studentId, classroom.getStudents().size(), classroom.getMaxStudents());
    }

    @Override
    @Transactional
    public void removeStudent(UUID userId, UUID classroomId, UUID studentId) {
        log.info("Removendo estudante da turma. [requisitanteId={}] [classroomId={}] [studentId={}]",
                userId, classroomId, studentId);

        School school = schoolService.findByUserId(userId);
        ensureSchoolHasActiveSubscription(school.getId());

        Classroom classroom = findById(classroomId);
        ensureClassroomBelongsToSchool(school.getId(), classroom);

        Student student = studentService.findById(studentId);
        ensureStudentBelongsToSchool(school.getId(), student);
        ensureClassroomContainsStudent(classroom, student);

        classroom.getStudents().remove(student);
        classroomRepository.save(classroom);

        log.info("Estudante removido da turma com sucesso. [classroomId={}] [studentId={}]",
                classroomId, studentId);
    }

    @Override
    @Transactional
    public void deleteById(UUID userId, UUID classroomId) {
        log.info("Iniciando exclusão de turma. [requisitanteId={}] [classroomId={}]",
                userId, classroomId);

        School school = schoolService.findByUserId(userId);
        ensureSchoolHasActiveSubscription(school.getId());

        Classroom classroom = findById(classroomId);
        ensureClassroomBelongsToSchool(school.getId(), classroom);

        if (!classroom.getStudents().isEmpty()) {
            log.warn("Tentativa de excluir turma com estudantes vinculados. [classroomId={}] [totalEstudantes={}]",
                    classroomId, classroom.getStudents().size());
            throw new BusinessException("Não é possivel apagar turma com estudantes");
        }

        classroomRepository.deleteById(classroom.getId());

        log.info("Turma excluída com sucesso. [classroomId={}] [schoolId={}]",
                classroomId, school.getId());
    }

    private void ensureIsIndividual(ClassType classType, ClassroomRequest request) {
        boolean isIndividual = classType.getId() == ClassType.Values.INDIVIDUAL.getValue();
        if (isIndividual && !(request.maxStudents() == 1)) {
            log.warn("Tentativa de criar turma individual com mais de 1 estudante. [classTypeId={}] [maxStudents={}]",
                    classType.getId(), request.maxStudents());
            throw new IllegalArgumentException("Turmas individuais devem ter apenas 1 estudante");
        }
    }

    private void ensureClassroomFull(Classroom classroom) {
        if (!(classroom.getStudents().size() < classroom.getMaxStudents())) {
            log.warn("Tentativa de adicionar estudante em turma cheia. [classroomId={}] [ocupacao={}/{}]",
                    classroom.getId(), classroom.getStudents().size(), classroom.getMaxStudents());
            throw new BusinessException("Turma cheia");
        }
    }

    private void ensureStudentCountIsBelowMax(Classroom classroom, ClassroomRequest request) {
        if (!(classroom.getStudents().size() < request.maxStudents())) {
            log.warn("Novo limite de estudantes menor que a quantidade atual na turma. [classroomId={}] [atual={}] [novoMax={}]",
                    classroom.getId(), classroom.getStudents().size(), request.maxStudents());
            throw new IllegalArgumentException("A turma possui quantidade acima do informado");
        }
    }

    private void ensureClassroomContainsStudent(Classroom classroom, Student student) {
        if (!classroom.getStudents().contains(student)) {
            log.warn("Tentativa de remover estudante que não pertence à turma. [classroomId={}] [studentId={}]",
                    classroom.getId(), student.getId());
            throw new BusinessException("Estudante não pertencer à turma");
        }
    }

    private void ensureSubjectBelongsToSchool(UUID schoolId, Subject subject) {
        if (!subject.getSchool().getId().equals(schoolId)) {
            log.warn("Tentativa de usar disciplina de outra escola. [subjectId={}] [subjectSchoolId={}] [schoolId={}]",
                    subject.getId(), subject.getSchool().getId(), schoolId);
            throw new AccessDeniedException("Não é possivel interagir com disciplina de outra escola");
        }
    }

    private void ensureClassroomBelongsToSchool(UUID schoolId, Classroom classroom) {
        if (!classroom.getSchool().getId().equals(schoolId)) {
            log.warn("Tentativa de acesso a turma de outra escola. [classroomId={}] [classroomSchoolId={}] [schoolId={}]",
                    classroom.getId(), classroom.getSchool().getId(), schoolId);
            throw new AccessDeniedException("Não é possivel interagir com turma de outra escola");
        }
    }

    private void ensureStudentBelongsToSchool(UUID schoolId, Student student) {
        if (!student.getSchool().getId().equals(schoolId)) {
            log.warn("Tentativa de acesso a estudante de outra escola. [studentId={}] [studentSchoolId={}] [schoolId={}]",
                    student.getId(), student.getSchool().getId(), schoolId);
            throw new AccessDeniedException("Não é possivel interagir com estudantes de outra escola");
        }
    }

    private void ensureSchoolHasActiveSubscription(UUID schoolId) {
        try {
            schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId);
        }
        catch (SubscriptionException e) {
            log.warn("Operação bloqueada: escola sem licença ativa. [schoolId={}]", schoolId);
            throw new SubscriptionException("A escola não possui licença ativa.");
        }
    }
}
