package com.system.core.application.domain.collaborator.mapper;

import com.system.core.application.domain.collaborator.Collaborator;
import com.system.core.application.domain.collaborator.dto.CollaboratorDetailResponse;
import com.system.core.application.domain.collaborator.dto.CollaboratorResponse;

public interface CollaboratorMapper {
    CollaboratorResponse toDto(Collaborator collaborator);
    CollaboratorDetailResponse toDtoDetail(Collaborator collaborator);
}
