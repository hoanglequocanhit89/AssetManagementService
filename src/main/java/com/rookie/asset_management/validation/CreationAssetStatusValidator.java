package com.rookie.asset_management.validation;

import com.rookie.asset_management.enums.AssetStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CreationAssetStatusValidator
    implements ConstraintValidator<ValidCreationAssetStatus, AssetStatus> {

  @Override
  public boolean isValid(AssetStatus value, ConstraintValidatorContext context) {
    return value == AssetStatus.AVAILABLE || value == AssetStatus.NOT_AVAILABLE;
  }
}
