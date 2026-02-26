package com.system.application.domain.collaborator.service;

import com.system.application.domain.collaborator.Collaborator;
import com.system.application.domain.collaborator.dto.*;
import com.system.application.domain.collaborator.repository.CollaboratorRepository;
import com.system.application.domain.role.Role;
import com.system.application.domain.school.School;
import com.system.application.domain.school.service.SchoolService;
import com.system.application.domain.user.User;
import com.system.application.domain.user.dto.UserRequest;
import com.system.application.domain.user.service.UserService;
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
public class CollaboratorServiceImpl implements CollaboratorService {
    private final CollaboratorRepository collaboratorRepository;
    private final UserService userService;
    private final SchoolService schoolService;
    private final BCryptPasswordEncoder passwordEncoder;

    public CollaboratorServiceImpl(
            CollaboratorRepository collaboratorRepository,
            UserService userService,
            SchoolService schoolService,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.collaboratorRepository = collaboratorRepository;
        this.userService = userService;
        this.schoolService = schoolService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Cacheable(value = "page_collaborators", key = "#userId + ':' + #page + ':' + #size")
    public PageResponse<CollaboratorResponse> findAllResponseBySchool(UUID userId, int page, int size) {
        School school = schoolService.findByUserId(userId);
        Pageable sortedPageable =
                PageRequest.of(page, size, Sort.by("user.username").ascending());
        Page<CollaboratorResponse> response = collaboratorRepository.findAllBySchoolId(school.getId(), sortedPageable)
                .map(c ->
                        new CollaboratorResponse(c.getId(), c.getUsername(), c.getSpecialty(), c.getWorkload()));
        return PageResponse.from(response);
    }

    @Override
    public Collaborator findById(UUID collaboratorId) {
        return collaboratorRepository.findById(collaboratorId)
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou colaborador"));
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
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou colaborador"));
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_collaborators", allEntries = true)
    public Collaborator save(UUID userId, UserRequest userRequest, CollaboratorRequest collaboratorRequest) {
        School school = schoolService.findByUserId(userId);
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
        return collaborator;
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_collaborators", allEntries = true)
    public void update(UUID userId, UUID collaboratorId, UpdateCollaboratorRequest updateRequest) {
        School school = schoolService.findByUserId(userId);
        Collaborator collaborator = collaboratorRepository.findById(collaboratorId)
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou colaborador"));
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
    }

    @Override
    @Transactional
    public void updatePassword(UUID userId, UUID collaboratorId, UpdateCollaboratorPasswordRequest passwordRequest) {
        School school = schoolService.findByUserId(userId);
        Collaborator collaborator = collaboratorRepository.findById(collaboratorId)
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou colaborador"));
        ensureCollaboratorBelongsToSchool(school.getId(), collaborator);
        collaborator.getUser().setPassword(passwordEncoder.encode(passwordRequest.newPassword()));
        collaboratorRepository.save(collaborator);
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_collaborators", allEntries = true)
    public void deleteById(UUID userId, UUID collaboratorId) {
        School school = schoolService.findByUserId(userId);
        Collaborator collaborator = collaboratorRepository.findById(collaboratorId)
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou colaborador"));
        ensureCollaboratorBelongsToSchool(school.getId(), collaborator);
        collaboratorRepository.deleteById(collaboratorId);
    }

    private void ensureCollaboratorBelongsToSchool(UUID schoolId, Collaborator collaborator) {
        if (!collaborator.getSchool().getId().equals(schoolId)) {
            throw new AccessDeniedException("Não pode alterar o colaborador de outra instituição");
        }
    }
}
