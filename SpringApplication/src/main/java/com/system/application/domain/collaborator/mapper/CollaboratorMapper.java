package com.system.application.domain.collaborator.mapper;

import com.system.application.domain.collaborator.Collaborator;
import com.system.application.domain.collaborator.dto.CollaboratorDetailResponse;
import com.system.application.domain.collaborator.dto.CollaboratorResponse;

public interface CollaboratorMapper {
    CollaboratorResponse toDto(Collaborator collaborator);
    CollaboratorDetailResponse toDtoDetail(Collaborator collaborator);
}
