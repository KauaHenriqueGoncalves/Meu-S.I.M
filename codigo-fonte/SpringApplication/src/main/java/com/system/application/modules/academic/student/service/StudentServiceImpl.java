package com.system.application.modules.academic.student.service;

import com.system.application.modules.identity.legalguardian.LegalGuardian;
import com.system.application.modules.identity.legalguardian.dto.LegalGuardianResponse;
import com.system.application.modules.identity.legalguardian.service.LegalGuardianService;
import com.system.application.modules.licensing.schoolsubscription.SchoolSubscription;
import com.system.application.modules.licensing.schoolsubscription.service.SchoolSubscriptionService;
import com.system.application.modules.school.School;
import com.system.application.modules.school.service.SchoolService;
import com.system.application.modules.academic.student.Student;
import com.system.application.modules.academic.student.dto.StudentDetailResponse;
import com.system.application.modules.academic.student.dto.StudentRequest;
import com.system.application.modules.academic.student.dto.StudentResponse;
import com.system.application.modules.academic.student.dto.UpdateStudentRequest;
import com.system.application.modules.academic.student.repository.StudentRepository;
import com.system.application.shared.dto.PageResponse;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.BusinessException;
import com.system.application.shared.exception.NotFoundObjectException;
import com.system.application.shared.exception.SubscriptionException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StudentServiceImpl implements StudentService {
    private static final Logger log =
            LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;
    private final SchoolSubscriptionService schoolSubscriptionService;
    private final SchoolService schoolService;
    private final LegalGuardianService legalGuardianService;

    public StudentServiceImpl(
            StudentRepository studentRepository,
            SchoolSubscriptionService schoolSubscriptionService,
            SchoolService schoolService,
            LegalGuardianService legalGuardianService
    ) {
        this.studentRepository = studentRepository;
        this.schoolSubscriptionService = schoolSubscriptionService;
        this.schoolService = schoolService;
        this.legalGuardianService = legalGuardianService;
    }

    @Override
    @Cacheable(value = "page_students", key = "#userId + ':' + #page + ':' + #size")
    public PageResponse<StudentResponse> findAllResponseBySchool(UUID userId, int page, int size) {
        School school = schoolService.findByUserId(userId);

        log.info("Buscando estudantes da escola. [requisitanteId={}] [schoolId={}] [page={}] [size={}]",
                userId, school.getId(), page, size);

        Pageable sortedPageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<StudentResponse> response = studentRepository.findAllBySchoolId(school.getId(), sortedPageable)
                .map(s -> new StudentResponse(s.getId(), s.getName(), s.getDateOfBirth(), s.getGrade()));

        log.info("Estudantes encontrados. [schoolId={}] [total={}] [totalPages={}]",
                school.getId(), response.getTotalElements(), response.getTotalPages());

        return PageResponse.from(response);
    }

    @Override
    public List<StudentResponse> findAllResponseByLegalGuardian(UUID userId, UUID legalGuardianId) {
        School school = schoolService.findByUserId(userId);

        log.info("Buscando estudantes do responsável. [requisitanteId={}] [legalGuardianId={}] [schoolId={}]",
                userId, legalGuardianId, school.getId());

        LegalGuardian legalGuardian = legalGuardianService.findById(legalGuardianId);
        ensureLegalGuardianBelongsToUserSchool(school.getId(), legalGuardian);

        List<StudentResponse> response = studentRepository.findAllByLegalGuardianId(legalGuardian.getId())
                .stream()
                .map(s -> new StudentResponse(s.getId(), s.getName(), s.getDateOfBirth(), s.getGrade()))
                .toList();

        log.info("Estudantes do responsável encontrados. [legalGuardianId={}] [total={}]",
                legalGuardianId, response.size());

        return response;
    }

    @Override
    public Student findById(UUID studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    log.warn("Estudante não encontrado. [studentId={}]", studentId);
                    return new NotFoundObjectException("Não encontrou estudante");
                });
    }

    @Override
    public StudentDetailResponse findResponseDetailById(UUID studentId) {
        return studentRepository.findById(studentId)
                .map(s -> new StudentDetailResponse(
                        s.getId(),
                        s.getName(),
                        s.getDateOfBirth(),
                        s.getGrade(),
                        new LegalGuardianResponse(
                                s.getLegalGuardian().getId(),
                                s.getLegalGuardian().getUser().getUsername(),
                                s.getLegalGuardian().getDegreeOfKinship()
                        ))
                )
                .orElseThrow(() -> {
                    log.warn("Estudante não encontrado ao buscar detalhes. [studentId={}]", studentId);
                    return new NotFoundObjectException("Não encontrou estudante");
                });
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_students", allEntries = true)
    public Student save(UUID userId, StudentRequest request) {
        School school = schoolService.findByUserId(userId);

        log.info("Iniciando cadastro de estudante. [requisitanteId={}] [schoolId={}] [nome={}] [legalGuardianId={}]",
                userId, school.getId(), request.name(), request.legalGuardianId());

        ensureSubscriptionSupportsStudentCount(school.getId());

        LegalGuardian legalGuardian = legalGuardianService.findById(request.legalGuardianId());
        ensureLegalGuardianBelongsToUserSchool(school.getId(), legalGuardian);

        Student student = new Student(
                null,
                school,
                request.name(),
                request.dateOfBirth(),
                request.grade(),
                legalGuardian
        );
        student = studentRepository.save(student);

        log.info("Estudante cadastrado com sucesso. [studentId={}] [schoolId={}] [legalGuardianId={}]",
                student.getId(), school.getId(), legalGuardian.getId());

        return student;
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_students", allEntries = true)
    public void update(UUID userId, UUID studentId, UpdateStudentRequest updateRequest) {
        log.info("Iniciando atualização de estudante. [requisitanteId={}] [studentId={}]",
                userId, studentId);

        School school = schoolService.findByUserId(userId);
        ensureSchoolHasActiveSubscription(school.getId());

        LegalGuardian legalGuardian = legalGuardianService.findById(updateRequest.legalGuardianId());
        ensureLegalGuardianBelongsToUserSchool(school.getId(), legalGuardian);

        Student student = findById(studentId);
        ensureStudentBelongsToUserSchool(school.getId(), student);

        student.setName(updateRequest.name());
        student.setDateOfBirth(updateRequest.dateOfBirth());
        student.setGrade(updateRequest.grade());
        student.setLegalGuardian(legalGuardian);

        log.info("Estudante atualizado com sucesso. [studentId={}] [schoolId={}] [legalGuardianId={}]",
                studentId, school.getId(), legalGuardian.getId());
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_students", allEntries = true)
    public void deleteById(UUID userId, UUID studentId) {
        log.info("Iniciando exclusão de estudante. [requisitanteId={}] [studentId={}]",
                userId, studentId);

        School school = schoolService.findByUserId(userId);
        ensureSchoolHasActiveSubscription(school.getId());

        Student student = findById(studentId);
        ensureStudentBelongsToUserSchool(school.getId(), student);

        studentRepository.deleteById(student.getId());

        log.info("Estudante excluído com sucesso. [studentId={}] [schoolId={}]",
                studentId, school.getId());
    }

    private void ensureLegalGuardianBelongsToUserSchool(UUID schoolId, LegalGuardian legalGuardian) {
        if (!legalGuardian.getSchool().getId().equals(schoolId)) {
            log.warn("Tentativa de vincular estudante a responsável de outra escola. [legalGuardianId={}] [legalGuardianSchoolId={}] [schoolId={}]",
                    legalGuardian.getId(), legalGuardian.getSchool().getId(), schoolId);
            throw new AccessDeniedException("Não pode alterar o responsável de outra instituição");
        }
    }

    private void ensureStudentBelongsToUserSchool(UUID schoolId, Student student) {
        if (!student.getSchool().getId().equals(schoolId)) {
            log.warn("Tentativa de acesso a estudante de outra escola. [studentId={}] [studentSchoolId={}] [schoolId={}]",
                    student.getId(), student.getSchool().getId(), schoolId);
            throw new AccessDeniedException("Não pode alterar o estudante de outra instituição");
        }
    }

    private void ensureSubscriptionSupportsStudentCount(UUID schoolId) {
        SchoolSubscription sub =
                schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId);
        long current = studentRepository.countBySchoolId(schoolId);
        if (current >= sub.getMaxStudents()) {
            log.warn("Limite de estudantes atingido para a licença ativa. [schoolId={}] [atual={}] [limite={}]",
                    schoolId, current, sub.getMaxStudents());
            throw new BusinessException("A licença não suporta o número de estudante");
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
