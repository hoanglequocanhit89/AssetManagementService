package com.rookie.asset_management.validation;

import com.rookie.asset_management.dto.request.UserRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AfterDOBValidator implements ConstraintValidator<AfterDOB, UserRequestDTO> {
  @Override
  public boolean isValid(UserRequestDTO user, ConstraintValidatorContext context) {
    if (user.getDob() == null || user.getJoinedDate() == null) return true;
    return user.getJoinedDate().isAfter(user.getDob());
  }
}
