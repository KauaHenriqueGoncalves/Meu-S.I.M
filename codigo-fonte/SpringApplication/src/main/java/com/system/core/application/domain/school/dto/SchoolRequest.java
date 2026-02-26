package com.system.core.application.domain.school.dto;

import com.system.core.application.shared.validation.NoLeadingTrailingSpace;
import com.system.core.application.shared.validation.ValidCnpj;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public record SchoolRequest(
        @NotBlank(message = "Código da escola não pode ser vazio")
        @Size(min=5, max = 50, message = "Código da escola deve ser menor que 50 caracteres")
        @NoLeadingTrailingSpace
        String nameCode,

        @NotBlank(message = "Nome da escola não pode ser vazio")
        @Size(min=5, max = 50, message = "Nome da escola deve ser menor que 50 caracteres")
        @NoLeadingTrailingSpace
        String schoolName,

        @NotBlank(message = "Cnpj não pode ser vazio")
        @Size(min = 14, max = 14, message = "Cnpj deve ter 14 caracteres")
        @ValidCnpj(message = "Cnpj deve ser válido")
        @NoLeadingTrailingSpace
        String cnpj
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
