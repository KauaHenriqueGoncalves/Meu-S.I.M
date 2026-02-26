package com.system.application.domain.student.service;

import com.system.application.domain.legalguardian.LegalGuardian;
import com.system.application.domain.legalguardian.dto.LegalGuardianResponse;
import com.system.application.domain.legalguardian.service.LegalGuardianService;
import com.system.application.domain.school.School;
import com.system.application.domain.school.service.SchoolService;
import com.system.application.domain.student.Student;
import com.system.application.domain.student.dto.StudentDetailResponse;
import com.system.application.domain.student.dto.StudentRequest;
import com.system.application.domain.student.dto.StudentResponse;
import com.system.application.domain.student.dto.UpdateStudentRequest;
import com.system.application.domain.student.repository.StudentRepository;
import com.system.application.shared.dto.PageResponse;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final SchoolService schoolService;
    private final LegalGuardianService legalGuardianService;

    public StudentServiceImpl(
            StudentRepository studentRepository,
            SchoolService schoolService,
            LegalGuardianService legalGuardianService
    ) {
        this.studentRepository = studentRepository;
        this.schoolService = schoolService;
        this.legalGuardianService = legalGuardianService;
    }

    // TODO: Criar metodo para buscar estudantes relacionados ao responsavel

    @Override
    @Cacheable(value = "page_students", key = "#userId + ':' + #page + ':' + #size")
    public PageResponse<StudentResponse> findAllResponseBySchool(UUID userId, int page, int size) {
        School school = schoolService.findByUserId(userId);
        Pageable sortedPageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<StudentResponse> response = studentRepository.findAllBySchoolId(school.getId(), sortedPageable)
                .map(s -> new StudentResponse(s.getId(), s.getName(), s.getDateOfBirth(), s.getGrade()));
        return PageResponse.from(response);
    }

    @Override
    public Student findById(UUID studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou estudante"));
    }

    @Override
    public StudentDetailResponse findResponseDetailById(UUID studentId) {
        return studentRepository.findById(studentId)
                .map(s -> {
                    return new StudentDetailResponse(
                            s.getId(),
                            s.getName(),
                            s.getDateOfBirth(),
                            s.getGrade(),
                            new LegalGuardianResponse(
                                    s.getLegalGuardian().getId(),
                                    s.getLegalGuardian().getUser().getUsername(),
                                    s.getLegalGuardian().getDegreeOfKinship()
                            ));
                })
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou estudante"));
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_students", allEntries = true)
    public Student save(UUID userId, StudentRequest request) {
        School school = schoolService.findByUserId(userId);
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
        return student;
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_students", allEntries = true)
    public void update(UUID userId, UUID studentId, UpdateStudentRequest updateRequest) {
        School school = schoolService.findByUserId(userId);
        LegalGuardian legalGuardian = legalGuardianService.findById(updateRequest.legalGuardianId());
        ensureLegalGuardianBelongsToUserSchool(school.getId(), legalGuardian);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou estudante"));
        ensureStudentBelongsToUserSchool(school.getId(), student);
        student.setName(updateRequest.name());
        student.setDateOfBirth(updateRequest.dateOfBirth());
        student.setGrade(updateRequest.grade());
        student.setLegalGuardian(legalGuardian);
        studentRepository.save(student);
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_students", allEntries = true)
    public void deleteById(UUID userId, UUID studentId) {
        School school = schoolService.findByUserId(userId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou estudante"));
        ensureStudentBelongsToUserSchool(school.getId(), student);
        studentRepository.deleteById(student.getId());
    }

    private void ensureLegalGuardianBelongsToUserSchool(UUID schoolId, LegalGuardian legalGuardian) {
        if (!legalGuardian.getSchool().getId().equals(schoolId)) {
            throw new AccessDeniedException("Não pode alterar o responsável de outra instituição");
        }
    }

    private void ensureStudentBelongsToUserSchool(UUID schoolId, Student student) {
        if (!student.getSchool().getId().equals(schoolId)) {
            throw new AccessDeniedException("Não pode alterar o estudante de outra instituição");
        }
    }
}
