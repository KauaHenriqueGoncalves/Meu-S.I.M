package com.system.application.shared.validation;

import com.system.application.shared.util.CpfValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public final class ValidCpfConstraintValidator
        implements ConstraintValidator<ValidCpf, String> {
    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || cpf.isBlank()) return false;
        return CpfValidator.getInstance().isValid(cpf);
    }
}