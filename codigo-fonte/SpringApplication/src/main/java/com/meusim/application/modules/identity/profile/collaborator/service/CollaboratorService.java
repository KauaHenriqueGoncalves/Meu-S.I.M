package com.meusim.application.modules.identity.profile.collaborator.service;

import com.meusim.application.modules.identity.profile.collaborator.Collaborator;
import com.meusim.application.modules.identity.base.user.dto.PasswordRequest;
import com.meusim.application.modules.identity.base.user.dto.CreateUserRequestDTO;
import com.meusim.application.modules.identity.profile.collaborator.dto.CollaboratorDetailResponse;
import com.meusim.application.modules.identity.profile.collaborator.dto.CollaboratorRequest;
import com.meusim.application.modules.identity.profile.collaborator.dto.CollaboratorResponse;
import com.meusim.application.modules.identity.profile.collaborator.dto.UpdateCollaboratorRequest;
import com.meusim.application.shared.dto.PageResponse;

import java.util.UUID;

public interface CollaboratorService {
    PageResponse<CollaboratorResponse> findAllResponseBySchool(UUID userId, String name, int page, int size);
    Collaborator findById(UUID collaboratorId);
    CollaboratorDetailResponse findResponseDetailById(UUID collaboratorId);
    Collaborator save(UUID userId, CreateUserRequestDTO createUserRequestDTO, CollaboratorRequest collaboratorRequest);
    void update(UUID userId, UUID collaboratorId, UpdateCollaboratorRequest updateRequest);
    void updatePassword(UUID userId, UUID collaboratorId, PasswordRequest passwordRequest);
    void deleteById(UUID userId, UUID collaboratorId);
}
