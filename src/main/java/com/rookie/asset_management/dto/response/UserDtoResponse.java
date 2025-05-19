package com.rookie.asset_management.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserDtoResponse {
  private Integer id;
  private String username;
  private String firstName;
  private String lastName;
  private String staffCode;
  private LocalDate joinedDate;
  private String type;
  private Boolean canDisable;
}
