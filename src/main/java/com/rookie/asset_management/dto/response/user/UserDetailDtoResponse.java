package com.rookie.asset_management.dto.response.user;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailDtoResponse {
  private Integer id;
  String staffCode;
  String username;
  LocalDate joinedDate;
  String location;
  String role;
  String firstName;
  String lastName;
  String fullName;
  LocalDate dob;
  String gender;
}
