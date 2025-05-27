package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.repository.UserRepository;
import com.rookie.asset_management.service.CustomUserDetailsService;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService, UserDetailsService {

  UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, username + " not found."));

    Set<GrantedAuthority> authorities =
        Stream.of(user.getRole())
            .map((role) -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toSet());

    return new org.springframework.security.core.userdetails.User(
        user.getUsername(), user.getPassword(), authorities);
  }
}
