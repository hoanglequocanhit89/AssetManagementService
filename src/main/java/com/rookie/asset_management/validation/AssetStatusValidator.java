package com.rookie.asset_management.validation;

import com.rookie.asset_management.enums.AssetStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * AssetStatusValidator is a custom validator that checks if the provided AssetStatus is one of the
 * valid states: AVAILABLE, NOT_AVAILABLE, WAITING, or RECYCLED. If the value is not valid, it
 * throws an AppException with a BAD_REQUEST status.
 */
public class AssetStatusValidator implements ConstraintValidator<ValidAssetStatus, AssetStatus> {

  @Override
  public boolean isValid(AssetStatus value, ConstraintValidatorContext context) {
    return value == AssetStatus.AVAILABLE
        || value == AssetStatus.NOT_AVAILABLE
        || value == AssetStatus.WAITING
        || value == AssetStatus.RECYCLED;
  }
}
