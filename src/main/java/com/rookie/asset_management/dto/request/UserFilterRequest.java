package com.rookie.asset_management.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserFilterRequest {
  private String name; //last name or first name
  private String staffCode; //staff code
  private String type; //user type
}
