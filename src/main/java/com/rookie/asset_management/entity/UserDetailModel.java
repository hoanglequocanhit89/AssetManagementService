package com.rookie.asset_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDetailModel implements UserDetails {

  String username;
  String password;

  Boolean isEnabled;
  Boolean isFirstLogin;
  List<GrantedAuthority> authorities;
  boolean accountNonExpired;
  boolean accountNonLocked;
  boolean credentialsNonExpired;

  @JsonIgnore User user;

  public UserDetailModel(User user) {
    this.username = user.getUsername();
    this.password = user.getPassword();
    this.isEnabled = !user.getDisabled();
    this.isFirstLogin = user.getFirstLogin() != null ? user.getFirstLogin() : false;
    this.authorities =
        user.getRole() != null
            ? List.of(new SimpleGrantedAuthority(user.getRole().getName()))
            : Collections.emptyList();
    this.accountNonExpired = true;
    this.accountNonLocked = true;
    this.credentialsNonExpired = true;
    this.user = user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return this.accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return this.credentialsNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return this.isEnabled;
  }
}
