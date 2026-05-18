package com.system.application.modules.identity.legalguardian.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.system.application.modules.identity.legalguardian.LegalGuardian;
import com.system.application.modules.identity.legalguardian.dto.*;
import com.system.application.modules.identity.legalguardian.repository.LegalGuardianRepository;
import com.system.application.modules.identity.role.Role;
import com.system.application.modules.identity.user.dto.PasswordRequest;
import com.system.application.modules.licensing.schoolsubscription.SchoolSubscription;
import com.system.application.modules.licensing.schoolsubscription.service.SchoolSubscriptionService;
import com.system.application.modules.school.School;
import com.system.application.modules.school.service.SchoolService;
import com.system.application.modules.identity.user.User;
import com.system.application.modules.identity.user.dto.UserRequest;
import com.system.application.modules.identity.user.service.UserService;
import com.system.application.shared.dto.PageResponse;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.BusinessException;
import com.system.application.shared.exception.NotFoundObjectException;
import com.system.application.shared.exception.SubscriptionException;
import com.system.application.shared.services.cache.CacheService;
import com.system.application.shared.services.cache.keys.CacheKeys;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
public class LegalGuardianServiceImpl implements LegalGuardianService {
    private static final Logger log =
            LoggerFactory.getLogger(LegalGuardianServiceImpl.class);

    private final LegalGuardianRepository legalGuardianRepository;
    private final SchoolSubscriptionService schoolSubscriptionService;
    private final UserService userService;
    private final SchoolService schoolService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CacheService cacheService;

    private static final Duration LEGAL_GUARDIAN_TTL = Duration.ofHours(15);

    public LegalGuardianServiceImpl(
            LegalGuardianRepository legalGuardianRepository,
            SchoolSubscriptionService schoolSubscriptionService,
            UserService userService,
            SchoolService schoolService,
            BCryptPasswordEncoder passwordEncoder,
            CacheService cacheService
    ) {
        this.legalGuardianRepository = legalGuardianRepository;
        this.schoolSubscriptionService = schoolSubscriptionService;
        this.userService = userService;
        this.schoolService = schoolService;
        this.passwordEncoder = passwordEncoder;
        this.cacheService = cacheService;
    }

    @Override
    public PageResponse<LegalGuardianResponse> findAllResponseBySchool(UUID userId, String name, int page, int size) {
        School school = schoolService.findByUserId(userId);

        String nameFilter = (name != null && !name.isBlank()) ? name.trim() : null;

        log.info("Buscando responsáveis da escola. [schoolId={}] [name={}] [page={}] [size={}]",
                school.getId(), nameFilter, page, size);

        String key = CacheKeys.legalGuardian(school.getId(), page, size, nameFilter);

        Optional<PageResponse<LegalGuardianResponse>> cacheResponse = cacheService.get(key, new TypeReference<>() {});

        if (cacheResponse.isPresent()) {
            log.info("Responsáveis encontrados no cache. [schoolId={}] [total={}] [totalPages={}]",
                    school.getId(), cacheResponse.get().totalElements(), cacheResponse.get().totalPages());
            return cacheResponse.get();
        }

        Pageable sortedPageable =
                PageRequest.of(page, size, Sort.by("user.username").ascending());

        Page<LegalGuardianResponse> responsePage =
                legalGuardianRepository.findAllBySchoolIdAndName(school.getId(), nameFilter, sortedPageable)
                        .map(l -> new LegalGuardianResponse(l.getId(), l.getUser().getUsername(), l.getDegreeOfKinship()));

        log.info("Responsáveis encontrados e serão enviado para cache. [schoolId={}] [total={}] [totalPages={}]",
                school.getId(), responsePage.getTotalElements(), responsePage.getTotalPages());

        PageResponse<LegalGuardianResponse> response = PageResponse.from(responsePage);

        cacheService.set(key, response, LEGAL_GUARDIAN_TTL);

        return response;
    }

    @Override
    public LegalGuardian findById(UUID legalGuardianId) {
        log.info("Buscando Responsável pelo id. [legalGuardianId={}]",
                legalGuardianId);

        return legalGuardianRepository.findById(legalGuardianId)
                .orElseThrow(() -> {
                    log.warn("Responsável não encontrado. [legalGuardianId={}]", legalGuardianId);
                    return new NotFoundObjectException("Não encontrou o responsável");
                });
    }

    @Override
    public LegalGuardianDetailResponse findResponseDetailById(UUID legalGuardianId) {
        String key = CacheKeys.legalGuardian(legalGuardianId, "responseDetail");

        log.info("Buscando detalhes do responsável. [legalGuardianId={}]",
                legalGuardianId);

        Optional<LegalGuardianDetailResponse> legalGuardianCache = cacheService.get(key, new TypeReference<>() {});

        if (legalGuardianCache.isPresent()) {
            log.info("Detalhes do responsável encontrado pelo cache. [legalGuardianId={}] [key={}]",
                    legalGuardianId, key);
            return legalGuardianCache.get();
        }

        LegalGuardian lg = findById(legalGuardianId);

        LegalGuardianDetailResponse response = LegalGuardianDetailResponse.of(lg);

        cacheService.set(key, response, LEGAL_GUARDIAN_TTL);

        return response;
    }

    @Override
    @Transactional
    public LegalGuardian save(UUID userId, UserRequest userRequest, LegalGuardianRequest legalGuardianRequest) {
        School school = schoolService.findByUserId(userId);

        log.info("Iniciando cadastro de responsável. [requisitanteId={}] [schoolId={}] [email={}]",
                userId, school.getId(), userRequest.email());

        ensureSubscriptionSupportsLegalGuardianCount(school.getId());

        User user = userService.registerUserWithRole(userRequest, Role.Values.LEGAL_GUARDIAN);

        LegalGuardian legalGuardian =
                new LegalGuardian(null, user, school, legalGuardianRequest.degreeOfKinship());

        legalGuardian = legalGuardianRepository.save(legalGuardian);

        log.info("Responsável cadastrado com sucesso. [legalGuardianId={}] [userId={}] [schoolId={}]",
                legalGuardian.getId(), user.getId(), school.getId());

        String key = CacheKeys.legalGuardianPattern(school.getId());

        log.info("Apagando todos os cache de Responsáveis ligado à escola. [school={}] [key={}]",
                school.getId(), key);

        cacheService.evictByPattern(key);

        return legalGuardian;
    }

    @Override
    @Transactional
    public void update(UUID userId, UUID legalGuardianId, UpdateLegalGuardianRequest updateRequest) {
        log.info("Iniciando atualização de responsável. [requisitanteId={}] [legalGuardianId={}]",
                userId, legalGuardianId);

        ensureLegalGuardianBelongsToUserSchool(userId, legalGuardianId);

        LegalGuardian legalGuardian = findById(legalGuardianId);
        UUID schoolId = legalGuardian.getSchool().getId();

        ensureSchoolHasActiveSubscription(schoolId);

        legalGuardian.getUser().setUsername(updateRequest.username());
        legalGuardian.getUser().setEmail(updateRequest.email());
        legalGuardian.getUser().setPhoneNumber(updateRequest.phoneNumber());
        legalGuardian.getUser().setAddress(updateRequest.address());
        legalGuardian.getUser().setActive(updateRequest.isActive());
        legalGuardian.setDegreeOfKinship(updateRequest.degreeOfKinship());
        legalGuardianRepository.save(legalGuardian);

        log.info("Responsável atualizado com sucesso. [legalGuardianId={}] [schoolId={}]",
                legalGuardianId, schoolId);

        String keySchool = CacheKeys.legalGuardianPattern(schoolId);
        String keyUser = CacheKeys.legalGuardianPattern(legalGuardianId);

        log.info("Apagando todos os cache de Responsáveis ligado à escola. [keySchool={}] [keyUser={}]",
                keySchool,  keyUser);

        cacheService.evictByPattern(keySchool);
        cacheService.evictByPattern(keyUser);
    }

    @Override
    @Transactional
    public void updatePassword(UUID userId, UUID legalGuardianId, PasswordRequest updateRequest) {
        log.info("Iniciando atualização de senha do responsável. [requisitanteId={}] [legalGuardianId={}]",
                userId, legalGuardianId);

        ensureLegalGuardianBelongsToUserSchool(userId, legalGuardianId);

        LegalGuardian legalGuardian = findById(legalGuardianId);

        legalGuardian.getUser().setPassword(passwordEncoder.encode(updateRequest.newPassword()));
        legalGuardianRepository.save(legalGuardian);

        log.info("Senha do responsável atualizada com sucesso. [legalGuardianId={}]", legalGuardianId);

        String key = CacheKeys.legalGuardianPattern(legalGuardian.getId());

        log.info("Apagando caches relacionado ao responsável. [legalGuardian={}] [key={}]",
                legalGuardian.getId(), key);

        cacheService.evictByPattern(key);
    }

    @Override
    @Transactional
    public void deleteById(UUID userId, UUID legalGuardianId) {
        log.info("Iniciando exclusão de responsável. [requisitanteId={}] [legalGuardianId={}]",
                userId, legalGuardianId);

        School school = schoolService.findByUserId(userId);

        ensureSchoolHasActiveSubscription(school.getId());
        ensureLegalGuardianBelongsToUserSchool(userId, legalGuardianId);

        legalGuardianRepository.deleteById(legalGuardianId);

        log.info("Responsável excluído com sucesso. [legalGuardianId={}] [schoolId={}]",
                legalGuardianId, school.getId());

        String keySchool = CacheKeys.legalGuardianPattern(school.getId());
        String keyUser = CacheKeys.legalGuardianPattern(legalGuardianId);

        log.info("Apagando todos os cache de Responsáveis ligado à escola. [keySchool={}] [keyUser={}]",
                keySchool,  keyUser);

        cacheService.evictByPattern(keySchool);
        cacheService.evictByPattern(keyUser);
    }

    private void ensureLegalGuardianBelongsToUserSchool(UUID userId, UUID legalGuardianId) {
        School school = schoolService.findByUserId(userId);
        boolean belongsToSchool =
                legalGuardianRepository.existsByIdAndSchoolId(legalGuardianId, school.getId());

        if (!belongsToSchool) {
            log.warn("Tentativa de acesso a responsável de outra instituição. [requisitanteId={}] [legalGuardianId={}] [schoolId={}]",
                    userId, legalGuardianId, school.getId());
            throw new AccessDeniedException("Não pode alterar o responsável de outra instituição");
        }
    }

    private void ensureSubscriptionSupportsLegalGuardianCount(UUID schoolId) {
        SchoolSubscription sub =
                schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId);
        long current = legalGuardianRepository.countBySchoolId(schoolId);
        if (current >= sub.getMaxLegalGuardian()) {
            log.warn("Limite de responsáveis atingido para a licença ativa. [schoolId={}] [atual={}] [limite={}]",
                    schoolId, current, sub.getMaxLegalGuardian());
            throw new BusinessException("A licença não suporta o número de reponsáveis");
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
