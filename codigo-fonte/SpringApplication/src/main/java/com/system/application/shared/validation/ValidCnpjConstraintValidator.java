package com.system.application.shared.validation;

import com.system.application.shared.util.CnpjValidator;
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
