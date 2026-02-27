package com.system.application.core.legalguardian.repository.projection;

import java.util.UUID;

public interface LegalGuardianListView {
    UUID getId();
    String getUsername();
    String getDegreeOfKinship();
}
