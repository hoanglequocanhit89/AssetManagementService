package com.rookie.asset_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntityAudit {
  @Column(unique = true, nullable = false)
  private String username;
  private String password;

  @ManyToOne
  @JoinColumn(name = "role_id")
  private Role role;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
  private UserProfile userProfile;

  @Column(name = "staff_code", length = 6, unique = true, nullable = false, columnDefinition = "CHAR(6)")
  // to ensure the staff code is stored as a fixed-length string
  private String staffCode;

  private Boolean disabled;

  @ManyToOne
  @JoinColumn(name = "location_id")
  private Location location;

  @Column(name = "first_login")
  private Boolean firstLogin;
  @Column(name = "joined_date")
  @DateTimeFormat(pattern = "dd-MM-yyyy")
  private LocalDate joinedDate;

  @OneToMany(mappedBy = "assignedBy", fetch = FetchType.LAZY)
  private List<Assignment> assignments;

  // No need to add assignmentTo here, because it is already in the Assignment entity
  // and at the user side, it is not necessary to have a list of assignments assigned to the user

  @OneToMany(mappedBy = "requestedBy", fetch = FetchType.LAZY)
  private List<ReturningRequest> returningRequests;

  @OneToMany(mappedBy = "acceptedBy", fetch = FetchType.LAZY)
  private List<ReturningRequest> returningAcceptedRequests;

  @Override
  public void prePersist() {
    super.prePersist();
    this.generatePassword();
    this.generateStaffCode();
  }

  private void generatePassword() {
    StringBuilder passwordBuilder = new StringBuilder();
    // auto generate password from username and date of birth
    passwordBuilder.append(this.username);
    passwordBuilder.append("@");
    // format the date of birth to ddMMyyyy
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
    passwordBuilder.append(this.userProfile.getDob().format(formatter));
    this.password = passwordBuilder.toString();
  }

  private void generateStaffCode() {
    // auto generate staff code
    StringBuilder staffCodeBuilder = new StringBuilder();
    // add the prefix "SD" to the staff code
    staffCodeBuilder.append("SD");
    // get the formatted id
    // convert the id to a string and then to a char array
    char[] idChars = String.valueOf(this.getId()).toCharArray();
    // add leading zeros to make it 4 digits
    int len = 4 - idChars.length;
    for (int i = 0; i < len; i++) {
      staffCodeBuilder.append("0");
    }
    staffCodeBuilder.append(this.getId());
    this.staffCode = staffCodeBuilder.toString();
  }
}
