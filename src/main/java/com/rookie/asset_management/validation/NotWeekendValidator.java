package com.rookie.asset_management.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class NotWeekendValidator implements ConstraintValidator<NotWeekend, LocalDate> {
  @Override
  public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
    if (date == null) return true;
    DayOfWeek day = date.getDayOfWeek();
    return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
  }
}
