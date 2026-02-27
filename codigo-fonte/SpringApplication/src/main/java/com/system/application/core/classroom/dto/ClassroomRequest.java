package com.system.application.core.classroom.dto;

import com.system.application.shared.validation.NoLeadingTrailingSpace;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record ClassroomRequest(
        @NotNull(message = "Selecione o tipo da classe")
        Long classTypeId,

        @NotNull(message = "Selecione uma disciplina")
        UUID subjectId,

        @NotNull(message = "Informe a quantidade máxima de estudante")
        @Min(value = 1, message = "A turma deve ter pelo menos 1 estudante")
        @Max(value = 999, message = "Número máximo da turma é 999 estudantes")
        Integer maxStudents,

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 60, message = "Nome da turma deve ter no máximo 30 caracteres")
        @NoLeadingTrailingSpace
        String name
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
