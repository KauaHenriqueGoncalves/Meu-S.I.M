package com.system.application.domain.collaborator.service;

import com.system.application.domain.collaborator.Collaborator;
import com.system.application.domain.collaborator.dto.CollaboratorRequest;
import com.system.application.domain.collaborator.dto.CollaboratorResponse;
import com.system.application.domain.collaborator.repository.CollaboratorRepository;
import com.system.application.domain.school.School;
import com.system.application.domain.schoolAdmin.SchoolAdmin;
import com.system.application.domain.schoolAdmin.service.SchoolAdminService;
import com.system.application.domain.user.User;
import com.system.application.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CollaboratorServiceImpl implements CollaboratorService {
    private final UserService userService;
    private final SchoolAdminService schoolAdminService;
    private final CollaboratorRepository collaboratorRepository;

    public CollaboratorServiceImpl(UserService userService,
                                   SchoolAdminService schoolAdminService,
                                   CollaboratorRepository collaboratorRepository) {
        this.userService = userService;
        this.schoolAdminService = schoolAdminService;
        this.collaboratorRepository = collaboratorRepository;
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
}
