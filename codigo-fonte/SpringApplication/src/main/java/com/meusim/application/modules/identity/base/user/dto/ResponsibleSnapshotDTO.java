package com.meusim.application.modules.identity.base.user.dto;

import java.util.UUID;

public record ResponsibleSnapshotDTO(
        UUID responsibleId,
        String username,
        String role
) { }
