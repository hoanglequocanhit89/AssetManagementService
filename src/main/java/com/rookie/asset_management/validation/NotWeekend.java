package com.rookie.asset_management.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NotWeekendValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotWeekend {
  String message() default "Joined date is Saturday or Sunday. Please select a different date";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
