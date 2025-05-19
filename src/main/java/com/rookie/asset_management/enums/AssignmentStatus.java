package com.rookie.asset_management.enums;

import lombok.Getter;

@Getter
public enum AssignmentStatus {
  WAITING_FOR_ACCEPTANCE("Waiting for Acceptance"),
  ACCEPTED("Accepted");

  private final String status;

  AssignmentStatus(String status) {
    this.status = status;
  }
}
