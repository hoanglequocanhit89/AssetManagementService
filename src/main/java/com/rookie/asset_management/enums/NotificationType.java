package com.rookie.asset_management.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
  ASSIGNMENT_CREATED,
  RETURN_REQUEST_CREATED,
  RETURN_REQUEST_COMPLETED,
  RETURN_REQUEST_REJECTED,
  ASSIGNMENT_ACCEPTED,
  ASSIGNMENT_REJECTED;
}
