package com.rookie.asset_management.enums;

import lombok.Getter;

@Getter
public enum AssignmentStatus {
  WAITING("waiting for acceptance"),
  ACCEPTED("accepted"),
  ;

  private final String status;

  AssignmentStatus(String status) {
    this.status = status;
  }
}
