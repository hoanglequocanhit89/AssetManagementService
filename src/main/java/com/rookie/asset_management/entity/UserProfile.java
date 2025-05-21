package com.rookie.asset_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rookie.asset_management.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
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
  @Column(columnDefinition = "GENDER")
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
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
