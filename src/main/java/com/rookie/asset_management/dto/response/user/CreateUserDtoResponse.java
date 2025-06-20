package com.rookie.asset_management.dto.response.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserDtoResponse {
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

  @JsonProperty("isSentEmail")
  boolean isSentEmail;
}
