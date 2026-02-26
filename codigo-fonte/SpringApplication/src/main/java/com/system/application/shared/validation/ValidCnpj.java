package com.system.application.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidCnpjConstraintValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCnpj {
    String message() default "Cnpj inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
