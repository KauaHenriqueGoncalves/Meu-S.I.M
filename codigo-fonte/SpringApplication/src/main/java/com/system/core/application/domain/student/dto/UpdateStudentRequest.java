package com.system.core.application.domain.student.dto;

import com.system.core.application.shared.validation.NoLeadingTrailingSpace;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateStudentRequest(
        @NotBlank(message = "Campo nome vazio")
        @Size(max = 100, message = "Nome deve conter no máximo 100 caracteres")
        @NoLeadingTrailingSpace
        String name,

        @NotNull(message = "Data de nascimento é obrigatória")
        @Past(message = "Data de nascimento deve ser no passado")
        LocalDate dateOfBirth,

        @NotBlank(message = "Campo serie vazio!")
        @Size(max = 20, message = "Campo serie deve conter no máximo 20 caracteres")
        @NoLeadingTrailingSpace
        String grade,

        @NotNull(message = "Selecione um responsável para o estudante")
        UUID legalGuardianId
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
