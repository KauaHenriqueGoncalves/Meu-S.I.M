package com.system.application.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NoLeadingTrailingSpaceValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface NoLeadingTrailingSpace {
    String message() default "Must not contain leading or trailing spaces";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
