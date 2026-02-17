package com.system.application.domain.subject.dto;

import com.system.application.shared.validation.NoLeadingTrailingSpace;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public record SubjectRequest(
        @NotBlank(message = "Disciplina está sem nome")
        @Size(max = 50, message = "Disciplina deve ter, no máximo, 50 caracteres")
        @NoLeadingTrailingSpace
        String name
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
