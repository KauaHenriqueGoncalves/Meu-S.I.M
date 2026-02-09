package com.system.application.domain.legalGuardian.dto;

import com.system.application.shared.validation.NoLeadingTrailingSpace;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public record UpdateLegalGuardianRequest(
        @NotBlank(message = "Username can't be blank")
        @Size(max = 100, message = "Username must be lower then 100")
        @NoLeadingTrailingSpace
        String username,

        @NotBlank(message = "Email can't be blank")
        @Email(message = "Email format incorrect")
        @Size(max = 255, message = "Email is up to 255")
        String email,

        @NotBlank(message = "PhoneNumber can't be blank")
        @Size(max = 20, message = "PhoneNumber is up to 255")
        @NoLeadingTrailingSpace
        String phoneNumber,

        @NotNull(message = "Address can't be null")
        @Size(max = 100, message = "Address must be lower then 100")
        @NoLeadingTrailingSpace
        String address,

        @NotBlank(message = "Address can't be blank")
        @Size(max = 30, message = "Degree of Kinship must be lower then 30")
        @NoLeadingTrailingSpace
        String degreeOfKinship
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
