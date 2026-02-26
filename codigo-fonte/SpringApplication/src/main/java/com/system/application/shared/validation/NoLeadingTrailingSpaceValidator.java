package com.system.application.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoLeadingTrailingSpaceValidator
        implements ConstraintValidator<NoLeadingTrailingSpace, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // deixa o @NotNull cuidar disso
        }
        return value.equals(value.trim());
    }
}