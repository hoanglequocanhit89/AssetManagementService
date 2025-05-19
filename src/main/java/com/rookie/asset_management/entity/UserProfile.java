package com.rookie.asset_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

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

  private Boolean gender;

  @OneToOne
  @MapsId
  @JoinColumn(name = "id")
  @JsonIgnore // to prevent circular reference
  private User user;

  public String getFullName() {
    return this.firstName + " " + this.lastName;
  }
}
