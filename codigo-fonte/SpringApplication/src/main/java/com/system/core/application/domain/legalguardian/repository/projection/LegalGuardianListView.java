package com.system.core.application.domain.legalguardian.repository.projection;

import java.util.UUID;

public interface LegalGuardianListView {
    UUID getId();
    String getUsername();
    String getDegreeOfKinship();
}
