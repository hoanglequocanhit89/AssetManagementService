package com.rookie.asset_management.enums;

import lombok.Getter;

@Getter
public enum Gender {
  MALE("male"),
  FEMALE("female");

  private final String label;

  Gender(String label) {
    this.label = label;
  }
}
