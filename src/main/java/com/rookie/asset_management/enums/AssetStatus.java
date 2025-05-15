package com.rookie.asset_management.enums;

import lombok.Getter;

@Getter
public enum AssetStatus {
  AVAILABLE("Available"),
  NOT_AVAILABLE("Not Available"),
  ASSIGNED("Assigned"),
  WAITING_FOR_RECYCLING("Waiting for Recycling"),
  RECYCLED("Recycled");

  private final String status;

  AssetStatus(String status) {
    this.status = status;
  }
}
