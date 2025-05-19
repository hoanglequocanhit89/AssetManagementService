package com.rookie.asset_management.dto.response;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

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
