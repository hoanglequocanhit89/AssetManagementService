package com.rookie.asset_management.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AgeOver18Validator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AgeOver18 {
  String message() default "User is under 18. Please select a different date";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
