package com.system.application.domain.collaborator.service;

import com.system.application.domain.collaborator.dto.*;
import com.system.application.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CollaboratorService {
    Page<CollaboratorResponse> findAllBySchoolAdminId(UUID adminId, Pageable pageable);
    CollaboratorDetailResponse findById(UUID id);
    UUID saveCollaborator(User user, UUID admin, CollaboratorRequest collaboratorRequest);
    UUID updateCollaborator(UUID adminId, UUID collaboratorId, UpdateCollaboratorRequest updateCollaboratorRequest);
    void updatePassword(UUID adminId, UUID collaboratorId, UpdateCollaboratorPasswordRequest updatePasswordRequest);
    void deleteById(UUID adminId, UUID collaboratorId);
}
