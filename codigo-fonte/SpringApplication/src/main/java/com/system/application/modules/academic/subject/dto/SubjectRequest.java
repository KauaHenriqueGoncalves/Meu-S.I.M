package com.system.application.modules.academic.subject.dto;

import com.system.application.shared.validation.NoEmoji;
import com.system.application.shared.validation.NoLeadingTrailingSpace;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public record SubjectRequest(
        @NotBlank(message = "Disciplina não pode ser vazio")
        @Size(max = 50, message = "Disciplina deve ser menor que 50 caracteres")
        @NoLeadingTrailingSpace
        @NoEmoji(message = "Não é permitido o recebimento de emoji")
        String name
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
