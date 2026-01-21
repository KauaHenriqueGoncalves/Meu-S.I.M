package com.system.application.domain.collaborator.service;

import com.system.application.domain.collaborator.Collaborator;
import com.system.application.domain.collaborator.dto.*;
import com.system.application.domain.collaborator.mapper.CollaboratorMapper;
import com.system.application.domain.collaborator.repository.CollaboratorRepository;
import com.system.application.domain.school.School;
import com.system.application.domain.schoolAdmin.SchoolAdmin;
import com.system.application.domain.schoolAdmin.service.SchoolAdminService;
import com.system.application.domain.user.User;
import com.system.application.domain.user.service.UserService;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CollaboratorServiceImpl implements CollaboratorService {
    private final UserService userService;
    private final SchoolAdminService schoolAdminService;
    private final CollaboratorMapper collaboratorMapper;
    private final CollaboratorRepository collaboratorRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public CollaboratorServiceImpl(UserService userService,
                                   SchoolAdminService schoolAdminService,
                                   CollaboratorMapper collaboratorMapper,
                                   CollaboratorRepository collaboratorRepository,
                                   BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.schoolAdminService = schoolAdminService;
        this.collaboratorMapper = collaboratorMapper;
        this.collaboratorRepository = collaboratorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<CollaboratorResponse> findAllBySchoolAdminId(UUID adminId, Pageable pageable) {
        SchoolAdmin schoolAdmin = schoolAdminService.findByUserId(adminId);
        UUID schoolId = schoolAdmin.getSchoolId().getId();
        return collaboratorRepository.findAllBySchoolId(schoolId, pageable)
                .map(c -> new CollaboratorResponse(
                        c.getId(),
                        c.getUsername(),
                        c.getSpecialty(),
                        c.getWorkload()
                ));
    }

    @Override
    public CollaboratorDetailResponse findById(UUID id) {
        Collaborator collaborator = collaboratorRepository.findById(id).orElseThrow(
                () -> new NotFoundObjectException("Not found Collaborator!")
        );
        return collaboratorMapper.toDtoDetail(collaborator);
    }

    @Override
    @Transactional
    public UUID saveCollaborator(User user, UUID adminId, CollaboratorRequest collaboratorRequest) {
        user = userService.saveColaborator(user);
        SchoolAdmin schoolAdmin = schoolAdminService.findByUserId(adminId);
        School school = schoolAdmin.getSchoolId();
        Collaborator collaborator = new Collaborator(
                null,
                user,
                school,
                collaboratorRequest.dateOfBirth(),
                collaboratorRequest.specialty(),
                collaboratorRequest.workload()
        );
        collaborator = collaboratorRepository.save(collaborator);
        return collaborator.getId();
    }

    @Override
    @Transactional
    public UUID updateCollaborator(UUID adminId, UUID collaboratorId, UpdateCollaboratorRequest updateCollaboratorRequest) {
        // TODO: Futuramente implementar que a conta fica desativada, caso troque de email
        SchoolAdmin schoolAdmin = schoolAdminService.findByUserId(adminId);
        Collaborator collaborator = collaboratorRepository.findById(collaboratorId).orElseThrow(
                () -> new NotFoundObjectException("Not found Collaborator")
        );
        Boolean isSameSchool = schoolAdmin.getSchoolId().getId() == collaborator.getSchool().getId();
        if (!isSameSchool) {
            throw new AccessDeniedException("Não pode alterar colaborador de outra instituição");
        }
        collaborator.getUser().setUsername(updateCollaboratorRequest.username());
        collaborator.getUser().setEmail(updateCollaboratorRequest.email());
        collaborator.getUser().setPhoneNumber(updateCollaboratorRequest.phoneNumber());
        collaborator.getUser().setAddress(updateCollaboratorRequest.address());
        collaborator.setDateOfBirth(updateCollaboratorRequest.dateOfBirth());
        collaborator.setSpecialty(updateCollaboratorRequest.specialty());
        collaborator.setWorkload(updateCollaboratorRequest.workload());
        collaborator = collaboratorRepository.save(collaborator);
        return collaborator.getId();
    }

    @Override
    @Transactional
    public void updatePassword(UUID adminId, UUID collaboratorId, UpdateCollaboratorPasswordRequest updatePasswordRequest) {
        SchoolAdmin schoolAdmin = schoolAdminService.findByUserId(adminId);
        Collaborator collaborator = collaboratorRepository.findById(collaboratorId).orElseThrow(
                () -> new NotFoundObjectException("Not found Collaborator")
        );
        Boolean isSameSchool = schoolAdmin.getSchoolId().getId() == collaborator.getSchool().getId();
        if (!isSameSchool) {
            throw new AccessDeniedException("Não pode alterar colaborador de outra instituição");
        }
        collaborator.getUser().setPassword(passwordEncoder.encode(updatePasswordRequest.newPassword()));
    }

    @Override
    @Transactional
    public void deleteById(UUID adminId, UUID collaboratorId) {
        SchoolAdmin schoolAdmin = schoolAdminService.findByUserId(adminId);
        Collaborator collaborator = collaboratorRepository.findById(collaboratorId).orElseThrow(
                () -> new NotFoundObjectException("Not found Collaborator")
        );
        Boolean isSameSchool = schoolAdmin.getSchoolId().getId() == collaborator.getSchool().getId();
        if (!isSameSchool) {
            throw new AccessDeniedException("Não pode alterar colaborador de outra instituição");
        }
        collaboratorRepository.deleteById(collaboratorId);
    }
}
