package com.rookie.asset_management.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntityAudit implements UserDetails {
  @Column(unique = true, nullable = false)
  private String username;

  private String password;

  @ManyToOne
  @JoinColumn(name = "role_id")
  private Role role;

  // exclude cascadeType remove and detach for soft delete without removing the user profile
  @OneToOne(
      mappedBy = "user",
      cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  private UserProfile userProfile;

  @Column(
      name = "staff_code",
      length = 6,
      unique = true,
      nullable = false,
      columnDefinition = "CHAR(6)")
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

  @OneToMany(mappedBy = "assignedTo", fetch = FetchType.LAZY)
  private List<Assignment> assignments;

  // No need to add assignmentBy here, because it is already in the Assignment entity
  // and at the user side, it is not necessary to have a list of assignments assigned to the other
  // user

  @OneToMany(mappedBy = "requestedBy", fetch = FetchType.LAZY)
  private List<ReturningRequest> returningRequests;

  @OneToMany(mappedBy = "acceptedBy", fetch = FetchType.LAZY)
  private List<ReturningRequest> returningAcceptedRequests;

  @Override
  public void prePersist() {
    super.prePersist();
    this.disabled = false;
    this.firstLogin = true;
    this.generatePassword();
  }

  @PostPersist
  public void postPersist() {
    // generate staff code after the user is persisted
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

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.role.getName().equalsIgnoreCase("ADMIN")
        ? List.of(() -> "ROLE_ADMIN")
        : List.of(() -> "ROLE_USER");
  }
}
