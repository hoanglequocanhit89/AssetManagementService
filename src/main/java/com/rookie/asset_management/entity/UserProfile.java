package com.rookie.asset_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rookie.asset_management.enums.Gender;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
public class UserProfile {
  @Id
  // No use of @GeneratedValue here as we are using @MapsId
  private Integer id;

  @Column(name = "first_name", length = 128)
  private String firstName;

  @Column(name = "last_name", length = 128)
  private String lastName;

  @DateTimeFormat(pattern = "dd-MM-yyyy") // format for date input
  private LocalDate dob;

  @Enumerated(EnumType.STRING)
  @Column(length = 10)
  private Gender gender;

  @OneToOne
  @MapsId
  @JoinColumn(name = "id")
  @JsonIgnore // to prevent circular reference
  private User user;

  public String getFullName() {
    return this.firstName + " " + this.lastName;
  }
}
