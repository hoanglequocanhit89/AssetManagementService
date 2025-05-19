package com.rookie.asset_management.dto.response.user;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDtoResponse {
  private Integer id;
  private String username;
  private String fullName;
  private String staffCode;
  private LocalDate joinedDate;
  private String role;
  private Boolean canDisable;
}
