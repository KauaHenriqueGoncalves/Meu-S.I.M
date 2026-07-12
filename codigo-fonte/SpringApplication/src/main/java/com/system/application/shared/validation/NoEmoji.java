package com.system.application.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoEmojiValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface NoEmoji {
    String message() default "Must not contain email in field";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
