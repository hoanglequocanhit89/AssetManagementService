package com.rookie.asset_management.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

public class AgeOver18Validator implements ConstraintValidator<AgeOver18, LocalDate> {
  @Override
  public boolean isValid(LocalDate dob, ConstraintValidatorContext context) {
    if (dob == null) return true; // Let @NotNull handle null cases
    return Period.between(dob, LocalDate.now()).getYears() >= 18;
  }
}
