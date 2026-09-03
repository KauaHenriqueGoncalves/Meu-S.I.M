package com.meusim.application.shared.validation;

import com.meusim.application.shared.util.CnpjValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public final class ValidCnpjConstraintValidator
        implements ConstraintValidator<ValidCnpj, String> {
    @Override
    public boolean isValid(String cnpj, ConstraintValidatorContext context) {
        if (cnpj == null) return true;
        if (cnpj.isBlank()) return false;
        return CnpjValidator.getInstance().isValid(cnpj);
    }
}
