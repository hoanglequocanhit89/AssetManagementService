package com.rookie.asset_management.enums;

import lombok.Getter;

@Getter
public enum ReturningRequestStatus {
  WAITING("Waiting for Returning"),
  COMPLETED("Completed");

  private final String status;

  ReturningRequestStatus(String status) {
    this.status = status;
  }
}
