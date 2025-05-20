package com.rookie.asset_management.dto.request.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserFilterRequest {
  private String query; // search query
  private String type; // user type
}
