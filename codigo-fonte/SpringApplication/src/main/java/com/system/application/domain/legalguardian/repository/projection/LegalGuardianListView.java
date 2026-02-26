package com.system.application.domain.legalguardian.repository.projection;

import java.util.UUID;

public interface LegalGuardianListView {
    UUID getId();
    String getUsername();
    String getDegreeOfKinship();
}
