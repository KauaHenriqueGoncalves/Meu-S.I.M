package com.system.application.modules.identity.collaborator.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.system.application.modules.identity.collaborator.Collaborator;
import com.system.application.modules.identity.collaborator.dto.*;
import com.system.application.modules.identity.collaborator.repository.CollaboratorRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
public class CollaboratorServiceImpl implements CollaboratorService {
    private static final Logger log =
            LoggerFactory.getLogger(CollaboratorServiceImpl.class);

    private final CollaboratorRepository collaboratorRepository;
    private final SchoolSubscriptionService schoolSubscriptionService;
    private final UserService userService;
    private final SchoolService schoolService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CacheService cacheService;

    private static final Duration COLLABORATOR_TTL = Duration.ofHours(15);

    public CollaboratorServiceImpl(
            CollaboratorRepository collaboratorRepository,
            SchoolSubscriptionService schoolSubscriptionService,
            UserService userService,
            SchoolService schoolService,
            BCryptPasswordEncoder passwordEncoder,
            CacheService cacheService
    ) {
        this.collaboratorRepository = collaboratorRepository;
        this.schoolSubscriptionService = schoolSubscriptionService;
        this.userService = userService;
        this.schoolService = schoolService;
        this.passwordEncoder = passwordEncoder;
        this.cacheService = cacheService;
    }

    @Override
    public PageResponse<CollaboratorResponse> findAllResponseBySchool(UUID userId, String name, int page, int size) {
        School school = schoolService.findByUserId(userId);

        String nameFilter = (name != null && !name.isBlank()) ? name.trim() : null;

        log.info("Buscando colaboradores da escola. [schoolId={}] [name={}] [page={}] [size={}]",
                school.getId(), nameFilter, page, size);

        String key = CacheKeys.collaborator(school.getId(), page, size, nameFilter);

        Optional<PageResponse<CollaboratorResponse>> cacheResponse = cacheService.get(key, new TypeReference<>(){});

        if (cacheResponse.isPresent()) {
            log.info("Colaboradores encontrados no cache. [schoolId={}] [total={}] [totalPages={}]",
                    school.getId(), cacheResponse.get().totalElements(), cacheResponse.get().totalPages());
            return cacheResponse.get();
        }

        Pageable sortedPageable =
                PageRequest.of(page, size, Sort.by("user.username").ascending());

        Page<CollaboratorResponse> responsePage =
                collaboratorRepository.findAllBySchoolIdAndName(school.getId(), nameFilter, sortedPageable)
                .map(c -> new CollaboratorResponse(c.getId(), c.getUser().getUsername(), c.getSpecialty(), c.getWorkload()));

        log.info("Colaboradores encontrados. [schoolId={}] [total={}] [totalPages={}]",
                school.getId(), responsePage.getTotalElements(), responsePage.getTotalPages());

        PageResponse<CollaboratorResponse> response = PageResponse.from(responsePage);

        cacheService.set(key, response, COLLABORATOR_TTL);

        return response;
    }

    @Override
    public Collaborator findById(UUID collaboratorId) {
        log.info("Buscando colaborador pelo id. [collaboratorId={}]",
                collaboratorId);

        return collaboratorRepository.findById(collaboratorId)
                .orElseThrow(() -> {
                    log.warn("Colaborador não encontrado. [collaboratorId={}]", collaboratorId);
                    return new NotFoundObjectException("Não encontrou colaborador");
                });
    }

    @Override
    public CollaboratorDetailResponse findResponseDetailById(UUID collaboratorId) {
        String key = CacheKeys.collaborator(collaboratorId, "detailResponse");

        log.info("Buscando detalhes do colaborador. [collaboratorId={}]",
                collaboratorId);

        Optional<CollaboratorDetailResponse> cacheResponse = cacheService.get(key, new TypeReference<>(){});

        if (cacheResponse.isPresent()) {
            log.info("Detalhes do colaborador encontrado pelo cache. [collaboratorId={}] [key={}]",
                    collaboratorId, key);
            return cacheResponse.get();
        }

        Collaborator c = findById(collaboratorId);

        CollaboratorDetailResponse response = CollaboratorDetailResponse.of(c);

        cacheService.set(key, response, COLLABORATOR_TTL);

        return response;
    }

    @Override
    @Transactional
    public Collaborator save(UUID userId, UserRequest userRequest, CollaboratorRequest collaboratorRequest) {
        School school = schoolService.findByUserId(userId);

        log.info("Iniciando cadastro de colaborador. [requisitanteId={}] [schoolId={}] [email={}]",
                userId, school.getId(), userRequest.email());

        ensureSubscriptionSupportsCollaboratorCount(school.getId());

        User user = userService.registerUserWithRole(userRequest, Role.Values.COLLABORATOR);

        Collaborator collaborator = new Collaborator(
                null,
                user,
                school,
                collaboratorRequest.dateOfBirth(),
                collaboratorRequest.specialty(),
                collaboratorRequest.workload()
        );

        collaborator = collaboratorRepository.save(collaborator);

        log.info("Colaborador cadastrado com sucesso. [collaboratorId={}] [userId={}] [schoolId={}]",
                collaborator.getId(), user.getId(), school.getId());

        String key = CacheKeys.collaboratorPattern(school.getId());

        log.info("Apagando todos os cache de Collaborator ligado à escola. [school={}] [key={}]",
                school.getId(), key);

        cacheService.evictByPattern(key);

        return collaborator;
    }

    @Override
    @Transactional
    public void update(UUID userId, UUID collaboratorId, UpdateCollaboratorRequest updateRequest) {
        log.info("Iniciando atualização de colaborador. [requisitanteId={}] [collaboratorId={}]",
                userId, collaboratorId);

        School school = schoolService.findByUserId(userId);

        Collaborator collaborator = findById(collaboratorId);
        ensureCollaboratorBelongsToSchool(school.getId(), collaborator);

        collaborator.getUser().setUsername(updateRequest.username());
        collaborator.getUser().setEmail(updateRequest.email());
        collaborator.getUser().setPhoneNumber(updateRequest.phoneNumber());
        collaborator.getUser().setAddress(updateRequest.address());
        collaborator.getUser().setActive(updateRequest.isActive());
        collaborator.setDateOfBirth(updateRequest.dateOfBirth());
        collaborator.setSpecialty(updateRequest.specialty());
        collaborator.setWorkload(updateRequest.workload());
        collaboratorRepository.save(collaborator);

        log.info("Colaborador atualizado com sucesso. [collaboratorId={}] [schoolId={}]",
                collaboratorId, school.getId());

        String keySchool = CacheKeys.collaboratorPattern(school.getId());
        String keyUser = CacheKeys.collaboratorPattern(collaborator.getId());

        log.info("Apagando todos os cache de colaborador ligado à escola. [keyPattern={}] [keyUser={}]",
                keySchool, keyUser);

        cacheService.evictByPattern(keySchool);
        cacheService.evictByPattern(keyUser);
    }

    @Override
    @Transactional
    public void updatePassword(UUID userId, UUID collaboratorId, PasswordRequest passwordRequest) {
        log.info("Iniciando atualização de senha do colaborador. [requisitanteId={}] [collaboratorId={}]",
                userId, collaboratorId);

        School school = schoolService.findByUserId(userId);
        Collaborator collaborator = findById(collaboratorId);
        ensureCollaboratorBelongsToSchool(school.getId(), collaborator);
        ensureSchoolHasActiveSubscription(school.getId());

        collaborator.getUser().setPassword(passwordEncoder.encode(passwordRequest.newPassword()));
        collaboratorRepository.save(collaborator);

        log.info("Senha do colaborador atualizada com sucesso. [collaboratorId={}] [schoolId={}]",
                collaboratorId, school.getId());

        String key = CacheKeys.collaboratorPattern(collaborator.getId());

        log.info("Apagando caches relacionado ao colaborador. [collaboratorId={}] [key={}]",
                collaboratorId, key);

        cacheService.evictByPattern(key);
    }

    @Override
    @Transactional
    public void deleteById(UUID userId, UUID collaboratorId) {
        log.info("Iniciando exclusão de colaborador. [requisitanteId={}] [collaboratorId={}]",
                userId, collaboratorId);

        School school = schoolService.findByUserId(userId);

        Collaborator collaborator = findById(collaboratorId);
        ensureCollaboratorBelongsToSchool(school.getId(), collaborator);

        collaboratorRepository.deleteById(collaboratorId);

        log.info("Colaborador excluído com sucesso. [collaboratorId={}] [schoolId={}]",
                collaboratorId, school.getId());

        String keySchool = CacheKeys.collaboratorPattern(school.getId());
        String keyUser = CacheKeys.collaboratorPattern(collaborator.getId());

        log.info("Apagando todos os cache de colaborador ligado à escola. [keyPattern={}] [keyUser={}]",
                keySchool, keyUser);

        cacheService.evictByPattern(keySchool);
        cacheService.evictByPattern(keyUser);
    }

    private void ensureCollaboratorBelongsToSchool(UUID schoolId, Collaborator collaborator) {
        if (!collaborator.getSchool().getId().equals(schoolId)) {
            log.warn("Tentativa de acesso a colaborador de outra instituição. [collaboratorId={}] [schoolId={}]",
                    collaborator.getId(), schoolId);
            throw new AccessDeniedException("Não pode alterar o colaborador de outra instituição");
        }
    }

    private void ensureSubscriptionSupportsCollaboratorCount(UUID schoolId) {
        SchoolSubscription sub =
                schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId);
        long current = collaboratorRepository.countBySchoolId(schoolId);

        if (current >= sub.getMaxCollaborators()) {
            log.warn("Limite de colaboradores atingido para a licença ativa. [schoolId={}] [atual={}] [limite={}]",
                    schoolId, current, sub.getMaxCollaborators());
            throw new BusinessException("A licença não suporta o número de colaboradores");
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
