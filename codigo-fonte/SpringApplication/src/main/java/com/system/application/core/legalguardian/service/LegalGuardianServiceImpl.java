package com.system.application.core.legalguardian.service;

import com.system.application.core.legalguardian.LegalGuardian;
import com.system.application.core.legalguardian.dto.*;
import com.system.application.core.legalguardian.repository.LegalGuardianRepository;
import com.system.application.core.role.Role;
import com.system.application.core.school.School;
import com.system.application.core.school.service.SchoolService;
import com.system.application.core.user.User;
import com.system.application.core.user.dto.UserRequest;
import com.system.application.core.user.service.UserService;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LegalGuardianServiceImpl implements LegalGuardianService {
    private final LegalGuardianRepository legalGuardianRepository;
    private final UserService userService;
    private final SchoolService schoolService;
    private final BCryptPasswordEncoder passwordEncoder;

    public LegalGuardianServiceImpl(
            LegalGuardianRepository legalGuardianRepository,
            UserService userService,
            SchoolService schoolService,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.legalGuardianRepository = legalGuardianRepository;
        this.userService = userService;
        this.schoolService = schoolService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Cacheable(value = "page_legal_guardians", key = "#userId + ':' + #page + ':' + #size")
    public PageResponse<LegalGuardianResponse> findAllResponseBySchool(UUID userId, int page, int size) {
        School school = schoolService.findByUserId(userId);
        Pageable sortedPageable =
                PageRequest.of(page, size, Sort.by("user.username").ascending());
        Page<LegalGuardianResponse> response = legalGuardianRepository.findAllBySchoolId(school.getId(), sortedPageable)
                .map(l -> new LegalGuardianResponse(l.getId(), l.getUsername(), l.getDegreeOfKinship()));
        return PageResponse.from(response);
    }

    @Override
    public LegalGuardian findById(UUID legalGuardianId) {
        return legalGuardianRepository.findById(legalGuardianId)
                .orElseThrow(() -> new NotFoundObjectException("Not found legal guardian"));
    }

    @Override
    public LegalGuardianDetailResponse findResponseDetailById(UUID legalGuardianId) {
        return legalGuardianRepository.findById(legalGuardianId)
                .map(lg -> {
                    return new LegalGuardianDetailResponse(
                            lg.getId(),
                            lg.getUser().getUsername(),
                            lg.getUser().getEmail(),
                            lg.getUser().getCpf(),
                            lg.getUser().getPhoneNumber(),
                            lg.getUser().getAddress(),
                            lg.getUser().getActive(),
                            lg.getDegreeOfKinship());
                })
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou o responsável"));
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_legal_guardians", allEntries = true)
    public LegalGuardian save(UUID userId, UserRequest userRequest, LegalGuardianRequest legalGuardianRequest) {
        School school = schoolService.findByUserId(userId);
        User user = userService.registerUserWithRole(userRequest, Role.Values.LEGAL_GUARDIAN);
        LegalGuardian legalGuardian =
                new LegalGuardian(null, user, school, legalGuardianRequest.degreeOfKinship());
        legalGuardian = legalGuardianRepository.save(legalGuardian);
        return legalGuardian;
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_legal_guardians", allEntries = true)
    public void update(UUID userId, UUID legalGuardianId, UpdateLegalGuardianRequest updateRequest) {
        ensureLegalGuardianBelongsToUserSchool(userId, legalGuardianId);
        LegalGuardian legalGuardian = legalGuardianRepository.findById(legalGuardianId)
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou o responsável"));
        legalGuardian.getUser().setUsername(updateRequest.username());
        legalGuardian.getUser().setEmail(updateRequest.email());
        legalGuardian.getUser().setPhoneNumber(updateRequest.phoneNumber());
        legalGuardian.getUser().setAddress(updateRequest.address());
        legalGuardian.getUser().setActive(updateRequest.isActive());
        legalGuardian.setDegreeOfKinship(updateRequest.degreeOfKinship());
        legalGuardianRepository.save(legalGuardian);
    }

    @Override
    @Transactional
    public void updatePassword(UUID userId, UUID legalGuardianId, UpdateLegalGuardianPasswordRequest updateRequest) {
        ensureLegalGuardianBelongsToUserSchool(userId, legalGuardianId);
        LegalGuardian legalGuardian = legalGuardianRepository.findById(legalGuardianId)
                .orElseThrow(() -> new NotFoundObjectException("Not found Legal Guardian"));
        legalGuardian.getUser().setPassword(passwordEncoder.encode(updateRequest.newPassword()));
        legalGuardianRepository.save(legalGuardian);
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_legal_guardians", allEntries = true)
    public void deleteById(UUID userId, UUID legalGuardianId) {
        ensureLegalGuardianBelongsToUserSchool(userId, legalGuardianId);
        legalGuardianRepository.deleteById(legalGuardianId);
    }

    private void ensureLegalGuardianBelongsToUserSchool(UUID userId, UUID legalGuardianId) {
        School school = schoolService.findByUserId(userId);
        boolean belongsToSchool =
                legalGuardianRepository.existsByIdAndSchoolId(legalGuardianId, school.getId());
        if (!belongsToSchool) {
            throw new AccessDeniedException("Não pode alterar o responsável de outra instituição");
        }
    }
}
