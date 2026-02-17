package com.system.application.domain.student.service;

import com.system.application.domain.legalGuardian.LegalGuardian;
import com.system.application.domain.legalGuardian.service.LegalGuardianService;
import com.system.application.domain.school.School;
import com.system.application.domain.schoolAdmin.SchoolAdmin;
import com.system.application.domain.schoolAdmin.service.SchoolAdminService;
import com.system.application.domain.student.Student;
import com.system.application.domain.student.dto.StudentRequest;
import com.system.application.domain.student.dto.StudentResponse;
import com.system.application.domain.student.dto.UpdateStudentRequest;
import com.system.application.domain.student.mapper.StudentMapper;
import com.system.application.domain.student.repository.StudentRepository;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StudentServiceImpl implements StudentService {
    private final LegalGuardianService legalGuardianService;
    private final SchoolAdminService schoolAdminService;
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public StudentServiceImpl(LegalGuardianService legalGuardianService,
                              SchoolAdminService schoolAdminService,
                              StudentRepository studentRepository,
                              StudentMapper studentMapper) {
        this.legalGuardianService = legalGuardianService;
        this.schoolAdminService = schoolAdminService;
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
    }

    @Override
    @Cacheable(key = "#adminId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize", value = "page_students")
    public Page<StudentResponse> findAllBySchoolAdminId(UUID adminId, Pageable pageable) {
        UUID schoolId = schoolAdminService.findSchoolIdByUserId(adminId);
        return studentRepository.findAllBySchool_Id(schoolId, pageable).map(studentMapper::toDto);
    }

    @Override
    public StudentResponse findById(UUID studentId) {
        return studentRepository.findById(studentId).map(studentMapper::toDto).orElseThrow(
                () -> new NotFoundObjectException("Not Found Student")
        );
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_students", allEntries = true)
    public UUID save(UUID adminId, StudentRequest studentRequest) {
        legalGuardianService.validateLegalGuardianBelongsToSchool(adminId, studentRequest.legalGuardianId());
        LegalGuardian legalGuardian = legalGuardianService.findByIdEntity(studentRequest.legalGuardianId());
        School school = legalGuardian.getSchool();
        Student student = new Student(
                null,
                school,
                studentRequest.name(),
                studentRequest.dateOfBirth(),
                studentRequest.grade(),
                legalGuardian
        );
        student = studentRepository.save(student);
        return student.getId();
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_students", allEntries = true)
    public UUID update(UUID adminId, UUID studentId, UpdateStudentRequest updateRequest) {
        legalGuardianService.validateLegalGuardianBelongsToSchool(adminId, updateRequest.legalGuardianId());
        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new NotFoundObjectException("Not Found Student")
        );
        LegalGuardian legalGuardian = legalGuardianService.findByIdEntity(updateRequest.legalGuardianId());
        Boolean isValid = student.getSchool().getId().equals(legalGuardian.getSchool().getId());
        if (!isValid) {
            throw new AccessDeniedException("Não pode alterar o estudante de outra instituição");
        }
        student.setName(updateRequest.name());
        student.setDateOfBirth(updateRequest.dateOfBirth());
        student.setGrade(updateRequest.grade());
        student.setLegalGuardian(legalGuardian);
        student = studentRepository.save(student);
        return student.getId();
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_students", allEntries = true)
    public void deleteById(UUID adminId, UUID studentId) {
        SchoolAdmin schoolAdmin = schoolAdminService.findByUserId(adminId);
        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new NotFoundObjectException("Not Found Student")
        );
        Boolean isValid = student.getSchool().getId().equals(schoolAdmin.getSchoolId().getId());
        if (!isValid) {
            throw new AccessDeniedException("Não pode alterar o estudante de outra instituição");
        }
        studentRepository.deleteById(student.getId());
    }
}
