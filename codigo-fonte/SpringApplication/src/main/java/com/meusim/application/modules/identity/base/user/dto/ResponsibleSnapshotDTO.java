package com.meusim.application.modules.identity.base.user.dto;

import java.util.UUID;

public record ResponsibleSnapshotDTO(
        UUID responsibleId, // <- collaboratorId || schoolAdminId
        String username,
        String role // <- SCOPE_collaborator || SCOPE_school_admin
) { }
