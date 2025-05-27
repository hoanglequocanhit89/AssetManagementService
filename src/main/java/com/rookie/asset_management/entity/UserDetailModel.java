package com.rookie.asset_management.entity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailModel implements UserDetails {

  String username;
  String password;
  Boolean isEnabled;
  List<GrantedAuthority> authorities;

  public UserDetailModel(User user) {
    this.username = user.getUsername();
    this.password = user.getPassword();
    this.isEnabled = !user.getDisabled();
    this.authorities =
        Stream.of(user.getRole().getName())
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
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
    return false;
  }

  @Override
  public boolean isAccountNonLocked() {
    return false;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return false;
  }

  @Override
  public boolean isEnabled() {
    return this.isEnabled;
  }
}
