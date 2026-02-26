package com.system.core.application.shared.validation;

import com.system.core.application.shared.util.CnpjValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCnpjConstraintValidator
        implements ConstraintValidator<ValidCnpj, String> {
    @Override
    public boolean isValid(String cnpj, ConstraintValidatorContext context) {
        if (cnpj == null || cnpj.isBlank()) return false;
        return CnpjValidator.getInstance().isValid(cnpj);
    }
}
