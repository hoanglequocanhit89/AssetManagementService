package com.rookie.asset_management.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CreationAssetStatusValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCreationAssetStatus {
  String message() default "Asset state must be AVAILABLE or NOT_AVAILABLE";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
