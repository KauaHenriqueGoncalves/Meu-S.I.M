package com.system.core.application.domain.collaborator.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public record CollaboratorDetailResponse(
        UUID id,
        String username,
        String email,
        String cpf,
        String phoneNumber,
        String address,
        LocalDate dateOfBirth,
        String specialty,
        String workload
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}