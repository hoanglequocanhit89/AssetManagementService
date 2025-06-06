package com.rookie.asset_management.dto.request;

import com.rookie.asset_management.enums.Gender;
import com.rookie.asset_management.validation.AfterDOB;
import com.rookie.asset_management.validation.AgeOver18;
import com.rookie.asset_management.validation.NotWeekend;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AfterDOB
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequestDTO {
  @NotBlank(message = "First name must not be blank")
  @NotNull(message = "First name is required")
  @Size(max = 128, message = "First name must not exceed 128 characters")
  @Pattern(regexp = "^[a-zA-Z ]*$", message = "First name must not contain special characters")
  String firstName;

  @NotBlank(message = "Last name must not be blank")
  @NotNull(message = "Last name is required")
  @Size(max = 128, message = "Last name must not exceed 128 characters")
  @Pattern(regexp = "^[a-zA-Z ]*$", message = "Last name must not contain special characters")
  String lastName;

  @NotNull(message = "Gender is required")
  Gender gender;

  @NotNull(message = "Email is required")
  @Email(message = "Invalid email format")
  String email;

  @NotNull(message = "This field is required")
  @Past(message = "Date of Birth must be in the past")
  @AgeOver18
  @DateTimeFormat(pattern = "dd-MM-yyyy")
  LocalDate dob;

  @NotNull(message = "Joined Date is required")
  @DateTimeFormat(pattern = "dd-MM-yyyy")
  @NotWeekend
  LocalDate joinedDate;

  String type;

  String location;
}
