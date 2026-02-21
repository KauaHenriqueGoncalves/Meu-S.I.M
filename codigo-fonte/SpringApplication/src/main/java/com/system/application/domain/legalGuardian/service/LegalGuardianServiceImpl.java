package com.system.application.domain.legalGuardian.service;

import com.system.application.domain.legalGuardian.LegalGuardian;
import com.system.application.domain.legalGuardian.dto.*;
import com.system.application.domain.legalGuardian.mapper.LegalGuardianMapper;
import com.system.application.domain.legalGuardian.repository.LegalGuardianRepository;
import com.system.application.domain.school.School;
import com.system.application.domain.schoolAdmin.SchoolAdmin;
import com.system.application.domain.schoolAdmin.service.SchoolAdminService;
import com.system.application.domain.user.User;
import com.system.application.domain.user.service.UserService;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LegalGuardianServiceImpl implements LegalGuardianService {
    private final UserService userService;
    private final SchoolAdminService schoolAdminService;
    private final LegalGuardianRepository legalGuardianRepository;
    private final LegalGuardianMapper legalGuardianMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public LegalGuardianServiceImpl(UserService userService,
                                    SchoolAdminService schoolAdminService,
                                    LegalGuardianRepository legalGuardianRepository,
                                    LegalGuardianMapper legalGuardianMapper,
                                    BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.schoolAdminService = schoolAdminService;
        this.legalGuardianRepository = legalGuardianRepository;
        this.legalGuardianMapper = legalGuardianMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Cacheable(key = "#adminId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize", value = "page_legal_guardians")
    public Page<LegalGuardianResponse> findAllBySchoolAdminId(UUID adminId, Pageable pageable) {
        UUID schoolId = schoolAdminService.findSchoolIdByUserId(adminId);

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("user.username").ascending()
        );

        return legalGuardianRepository.findAllBySchoolId(schoolId, sortedPageable)
                .map(l ->
                        new LegalGuardianResponse(
                            l.getId(),
                            l.getUsername(),
                            l.getDegreeOfKinship()
                        )
                );
    }

    @Override
    public LegalGuardianDetailResponse findById(UUID id) {
        LegalGuardian legalGuardian = legalGuardianRepository.findById(id).orElseThrow(
                () -> new NotFoundObjectException("Not found Legal Guardian")
        );
        return legalGuardianMapper.toDtoDetail(legalGuardian);
    }

    @Override
    public LegalGuardian findByIdEntity(UUID id) {
        return legalGuardianRepository.findById(id).orElseThrow(
                () -> new NotFoundObjectException("Not found legal guardian")
        );
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_legal_guardians", allEntries = true)
    public UUID saveLegalGuardian(User user, UUID adminId, LegalGuardianRequest legalGuardianRequest) {
        user = userService.saveLegalGuardian(user);
        SchoolAdmin schoolAdmin = schoolAdminService.findByUserId(adminId);
        School school = schoolAdmin.getSchoolId();
        LegalGuardian legalGuardian = new LegalGuardian(
                null,
                user,
                school,
                legalGuardianRequest.degreeOfKinship()
        );
        legalGuardian = legalGuardianRepository.save(legalGuardian);
        return legalGuardian.getId();
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_legal_guardians", allEntries = true)
    public UUID updateLegalGuardian(UUID adminId, UUID legalGuardianId, UpdateLegalGuardianRequest updateLegalGuardianRequest) {
        validateLegalGuardianBelongsToSchool(adminId, legalGuardianId);
        LegalGuardian legalGuardian = legalGuardianRepository.findById(legalGuardianId).orElseThrow(
                () -> new NotFoundObjectException("Not found Legal Guardian")
        );
        legalGuardian.getUser().setUsername(updateLegalGuardianRequest.username());
        legalGuardian.getUser().setEmail(updateLegalGuardianRequest.email());
        legalGuardian.getUser().setPhoneNumber(updateLegalGuardianRequest.phoneNumber());
        legalGuardian.getUser().setAddress(updateLegalGuardianRequest.address());
        legalGuardian.setDegreeOfKinship(updateLegalGuardianRequest.degreeOfKinship());
        legalGuardian = legalGuardianRepository.save(legalGuardian);
        return legalGuardian.getId();
    }

    @Override
    @Transactional
    public void updatePassword(UUID adminId, UUID legalGuardianId, UpdateLegalGuardianPasswordRequest updateLegalGuardianPassword) {
        validateLegalGuardianBelongsToSchool(adminId, legalGuardianId);
        LegalGuardian legalGuardian = legalGuardianRepository.findById(legalGuardianId).orElseThrow(
                () -> new NotFoundObjectException("Not found Legal Guardian")
        );
        legalGuardian.getUser().setPassword(passwordEncoder.encode(updateLegalGuardianPassword.newPassword()));
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_legal_guardians", allEntries = true)
    public void deleteById(UUID adminId, UUID legalGuardianId) {
        validateLegalGuardianBelongsToSchool(adminId, legalGuardianId);
        legalGuardianRepository.deleteById(legalGuardianId);
    }

    @Override
    public void validateLegalGuardianBelongsToSchool(UUID adminId, UUID legalGuardianId) {
        UUID schoolId = schoolAdminService.findSchoolIdByUserId(adminId);
        Boolean belongsToSchool = legalGuardianRepository.existsByIdAndSchool_Id(legalGuardianId, schoolId);
        if (!belongsToSchool) {
            throw new AccessDeniedException("Não pode alterar o responsável de outra instituição");
        }
    }
}
