package com.system.application.modules.identity.collaborator.repository.projection;

import java.util.UUID;

public interface CollaboratorListView {
    UUID getId();
    String getUsername();
    String getSpecialty();
    String getWorkload();
}