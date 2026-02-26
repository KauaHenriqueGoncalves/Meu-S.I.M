package com.system.application.domain.collaborator.service;

import com.system.application.domain.collaborator.Collaborator;
import com.system.application.domain.collaborator.dto.*;
import com.system.application.domain.user.dto.UserRequest;
import com.system.application.shared.dto.PageResponse;

import java.util.UUID;

public interface CollaboratorService {
    PageResponse<CollaboratorResponse> findAllResponseBySchool(UUID userId, int page, int size);
    Collaborator findById(UUID collaboratorId);
    CollaboratorDetailResponse findResponseDetailById(UUID collaboratorId);
    Collaborator save(UUID userId, UserRequest userRequest, CollaboratorRequest collaboratorRequest);
    void update(UUID userId, UUID collaboratorId, UpdateCollaboratorRequest updateRequest);
    void updatePassword(UUID userId, UUID collaboratorId, UpdateCollaboratorPasswordRequest passwordRequest);
    void deleteById(UUID userId, UUID collaboratorId);
}
