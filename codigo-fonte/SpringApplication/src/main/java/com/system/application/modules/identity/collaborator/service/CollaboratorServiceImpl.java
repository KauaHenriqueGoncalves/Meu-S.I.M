package com.system.application.modules.identity.collaborator.service;

import com.system.application.modules.identity.collaborator.Collaborator;
import com.system.application.modules.identity.collaborator.dto.*;
import com.system.application.modules.identity.collaborator.repository.CollaboratorRepository;
import com.system.application.modules.identity.role.Role;
import com.system.application.modules.identity.user.dto.PasswordRequest;
import com.system.application.modules.licensing.schoolsubscription.SchoolSubscription;
import com.system.application.modules.licensing.schoolsubscription.dto.SubscriptionInfoResponse;
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
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class CollaboratorServiceImpl implements CollaboratorService {
    private static final Logger log =
            LoggerFactory.getLogger(CollaboratorServiceImpl.class);

    private final CollaboratorRepository collaboratorRepository;
    private final SchoolSubscriptionService schoolSubscriptionService;
    private final UserService userService;
    private final SchoolService schoolService;
    private final BCryptPasswordEncoder passwordEncoder;

    public CollaboratorServiceImpl(
            CollaboratorRepository collaboratorRepository,
            SchoolSubscriptionService schoolSubscriptionService,
            UserService userService,
            SchoolService schoolService,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.collaboratorRepository = collaboratorRepository;
        this.schoolSubscriptionService = schoolSubscriptionService;
        this.userService = userService;
        this.schoolService = schoolService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Cacheable(value = "page_collaborators", key = "#userId + ':' + #page + ':' + #size")
    public PageResponse<CollaboratorResponse> findAllResponseBySchool(UUID userId, int page, int size) {
        School school = schoolService.findByUserId(userId);

        log.info("Buscando colaboradores da escola. [schoolId={}] [page={}] [size={}]",
                school.getId(), page, size);

        Pageable sortedPageable =
                PageRequest.of(page, size, Sort.by("user.username").ascending());
        Page<CollaboratorResponse> response = collaboratorRepository.findAllBySchoolId(school.getId(), sortedPageable)
                .map(c ->
                        new CollaboratorResponse(c.getId(), c.getUsername(), c.getSpecialty(), c.getWorkload()));

        log.info("Colaboradores encontrados. [schoolId={}] [total={}] [totalPages={}]",
                school.getId(), response.getTotalElements(), response.getTotalPages());

        return PageResponse.from(response);
    }

    @Override
    public Collaborator findById(UUID collaboratorId) {
        return collaboratorRepository.findById(collaboratorId)
                .orElseThrow(() -> {
                    log.warn("Colaborador não encontrado. [collaboratorId={}]", collaboratorId);
                    return new NotFoundObjectException("Não encontrou colaborador");
                });
    }

    @Override
    public CollaboratorDetailResponse findResponseDetailById(UUID collaboratorId) {
        return collaboratorRepository.findById(collaboratorId)
                .map(c -> {
                    return new CollaboratorDetailResponse(
                            c.getId(),
                            c.getUser().getUsername(),
                            c.getUser().getEmail(),
                            c.getUser().getCpf(),
                            c.getUser().getPhoneNumber(),
                            c.getUser().getAddress(),
                            c.getUser().getActive(),
                            c.getDateOfBirth(),
                            c.getSpecialty(),
                            c.getWorkload()
                    );
                })
                .orElseThrow(() -> {
                    log.warn("Colaborador não encontrado ao buscar detalhes. [collaboratorId={}]", collaboratorId);
                    return new NotFoundObjectException("Não encontrou colaborador");
                });
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_collaborators", allEntries = true)
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

        return collaborator;
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_collaborators", allEntries = true)
    public void update(UUID userId, UUID collaboratorId, UpdateCollaboratorRequest updateRequest) {
        log.info("Iniciando atualização de colaborador. [requisitanteId={}] [collaboratorId={}]",
                userId, collaboratorId);

        School school = schoolService.findByUserId(userId);
        ensureSchoolHasActiveSubscription(school.getId());

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
    }

    @Override
    @Transactional
    public void updatePassword(UUID userId, UUID collaboratorId, PasswordRequest passwordRequest) {
        log.info("Iniciando atualização de senha do colaborador. [requisitanteId={}] [collaboratorId={}]",
                userId, collaboratorId);

        School school = schoolService.findByUserId(userId);
        Collaborator collaborator = findById(collaboratorId);
        ensureCollaboratorBelongsToSchool(school.getId(), collaborator);

        collaborator.getUser().setPassword(passwordEncoder.encode(passwordRequest.newPassword()));
        collaboratorRepository.save(collaborator);

        log.info("Senha do colaborador atualizada com sucesso. [collaboratorId={}] [schoolId={}]",
                collaboratorId, school.getId());
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_collaborators", allEntries = true)
    public void deleteById(UUID userId, UUID collaboratorId) {
        log.info("Iniciando exclusão de colaborador. [requisitanteId={}] [collaboratorId={}]",
                userId, collaboratorId);

        School school = schoolService.findByUserId(userId);
        ensureSchoolHasActiveSubscription(school.getId());

        Collaborator collaborator = findById(collaboratorId);
        ensureCollaboratorBelongsToSchool(school.getId(), collaborator);

        collaboratorRepository.deleteById(collaboratorId);

        log.info("Colaborador excluído com sucesso. [collaboratorId={}] [schoolId={}]",
                collaboratorId, school.getId());
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
