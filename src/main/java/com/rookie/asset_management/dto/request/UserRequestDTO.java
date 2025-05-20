package com.rookie.asset_management.dto.request;

import com.rookie.asset_management.enums.Gender;
import com.rookie.asset_management.validation.AfterDOB;
import com.rookie.asset_management.validation.AgeOver18;
import com.rookie.asset_management.validation.NotWeekend;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AfterDOB
public class UserRequestDTO {
  @NotBlank(message = "First name must not be blank")
  @NotNull(message = "First name is required")
  @Size(max = 128, message = "First name must not exceed 128 characters")
  @Pattern(regexp = "^[a-zA-Z ]*$", message = "First name must not contain special characters")
  private String firstName;

  @NotBlank(message = "Last name must not be blank")
  @NotNull(message = "Last name is required")
  @Size(max = 128, message = "Last name must not exceed 128 characters")
  @Pattern(regexp = "^[a-zA-Z ]*$", message = "Last name must not contain special characters")
  private String lastName;

  @NotNull(message = "Gender is required")
  private Gender gender;

  @NotNull(message = "This field is required")
  @Past(message = "Date of Birth must be in the past")
  @AgeOver18
  @DateTimeFormat(pattern = "dd-MM-yyyy")
  private LocalDate dob;

  @NotNull(message = "Joined Date is required")
  @DateTimeFormat(pattern = "dd-MM-yyyy")
  @NotWeekend
  private LocalDate joinedDate;

  private String type; // "Admin" or "Staff", default "Staff"
}
