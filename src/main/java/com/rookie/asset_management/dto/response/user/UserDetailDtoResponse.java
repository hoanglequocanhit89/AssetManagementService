package com.rookie.asset_management.dto.response.user;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailDtoResponse {
  Integer id;
  String staffCode;
  String username;
  String email;
  LocalDate joinedDate;
  String location;
  String role;
  String firstName;
  String lastName;
  String fullName;
  LocalDate dob;
  String gender;
}
