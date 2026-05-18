package com.system.application.modules.identity.collaborator.dto;

import com.system.application.modules.identity.collaborator.Collaborator;

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
        Boolean isActive,
        LocalDate dateOfBirth,
        String specialty,
        String workload

) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static CollaboratorDetailResponse of(Collaborator c) {
        return new CollaboratorDetailResponse(
                c.getId(),
                c.getUser().getUsername(),
                c.getUser().getEmail(),
                c.getUser().getCpf(),
                c.getUser().getPhoneNumber(),
                c.getUser().getAddress(),
                c.getUser().getActive(),
                c.getDateOfBirth(),
                c.getSpecialty(),
                c.getWorkload()
        );
    }
}