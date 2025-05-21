package com.rookie.asset_management.enums;

import lombok.Getter;

@Getter
public enum AssetStatus {
  AVAILABLE("Available"),
  NOT_AVAILABLE("Not Available"),
  ASSIGNED("Assigned"),
  WAITING("waiting for recycling"),
  RECYCLED("Recycled");

  private final String status;

  AssetStatus(String status) {
    this.status = status;
  }
}
