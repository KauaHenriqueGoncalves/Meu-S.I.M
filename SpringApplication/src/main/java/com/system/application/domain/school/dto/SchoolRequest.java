package com.system.application.domain.school.dto;

import com.system.application.shared.exception.CnpjInvalidException;
import com.system.application.shared.util.CnpjValidator;
import com.system.application.shared.validation.NoLeadingTrailingSpace;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public record SchoolRequest(
        @NotBlank(message = "nameCode can't be blank")
        @Size(min=5, max = 50, message = "nameCode must be lower then 50")
        @NoLeadingTrailingSpace
        String nameCode,

        @NotBlank(message = "schoolName can't be blank")
        @Size(min=5, max = 50, message = "schoolName must be lower then 50")
        @NoLeadingTrailingSpace
        String schoolName,

        @NotBlank(message = "cnpj can't be blank")
        @Size(min = 14, max = 14, message = "cnpj must have 14 characters.")
        @NoLeadingTrailingSpace
        String cnpj
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public SchoolRequest {
        if (!CnpjValidator.getInstance().isValid(cnpj)) {
            throw new CnpjInvalidException("Invalid CNPJ");
        }
    }
}
