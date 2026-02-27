package com.system.application.core.collaborator.repository.projection;

import java.util.UUID;

public interface CollaboratorListView {
    UUID getId();
    String getUsername();
    String getSpecialty();
    String getWorkload();
}