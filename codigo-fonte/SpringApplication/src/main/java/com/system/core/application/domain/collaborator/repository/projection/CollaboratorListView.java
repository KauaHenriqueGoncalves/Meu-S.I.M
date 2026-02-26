package com.system.core.application.domain.collaborator.repository.projection;

import java.util.UUID;

public interface CollaboratorListView {
    UUID getId();
    String getUsername();
    String getSpecialty();
    String getWorkload();
}