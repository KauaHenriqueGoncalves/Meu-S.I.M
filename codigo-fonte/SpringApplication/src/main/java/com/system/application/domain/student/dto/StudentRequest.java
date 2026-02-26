package com.system.application.domain.student.dto;

import com.system.application.shared.validation.NoLeadingTrailingSpace;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public record StudentRequest(
        @NotBlank(message = "Nome não pode ser vazio")
        @Size(max = 100, message = "Nome deve ser menor que 100 caracteres")
        @NoLeadingTrailingSpace
        String name,

        @NotNull(message = "Data de nascimento é obrigatório")
        @Past(message = "Data de nascimento deve ser no passado")
        LocalDate dateOfBirth,

        @NotBlank(message = "Turma do estudante deve ser informado")
        @Size(max = 20, message = "Turma do estudante deve ser menor que 20 caracteres")
        @NoLeadingTrailingSpace
        String grade,

        @NotNull(message = "Selecione um responsável para o estudante")
        UUID legalGuardianId
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
