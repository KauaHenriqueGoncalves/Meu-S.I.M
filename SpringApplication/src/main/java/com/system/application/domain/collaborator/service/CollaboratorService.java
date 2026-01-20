package com.system.application.domain.collaborator.service;

import com.system.application.domain.collaborator.dto.CollaboratorRequest;
import com.system.application.domain.collaborator.dto.CollaboratorResponse;
import com.system.application.domain.collaborator.repository.projection.CollaboratorListView;
import com.system.application.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CollaboratorService {
    Page<CollaboratorResponse> findAllBySchoolAdminId(@Param("adminId") UUID adminId, Pageable pageable);
    UUID saveCollaborator(User user, UUID admin, CollaboratorRequest collaboratorRequest);
}
