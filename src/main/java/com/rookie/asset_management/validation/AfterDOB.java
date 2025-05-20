package com.rookie.asset_management.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AfterDOBValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterDOB {
  String message() default
      "Joined date is not later than Date of Birth. Please select a different date";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
