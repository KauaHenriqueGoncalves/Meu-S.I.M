package com.system.application.domain.legalGuardian.repository.projection;

import java.util.UUID;

public interface LegalGuardianListView {
    UUID getId();
    String getUsername();
    String getDegreeOfKinship();
}
