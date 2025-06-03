package com.rookie.asset_management.config.security;

import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.service.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class LimitLoginAuthenticationProvider implements AuthenticationProvider {

  LoginAttemptService loginAttemptService;

  UserDetailsService userDetailsService;

  PasswordEncoder passwordEncoder;

  HttpServletRequest request;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    //    String key = SecurityUtils.getIP(request);
    String username = authentication.getName();
    if (loginAttemptService.isBlocked(username)) {
      throw new BadCredentialsException(
          "Your account is temporarily locked due to too many failed login attempts. Please try again later.");
    }

    String password = (String) authentication.getCredentials();

    try {
      UserDetails user = userDetailsService.loadUserByUsername(username);

      if (passwordEncoder.matches(password, user.getPassword())) {
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
      } else {
        throw new BadCredentialsException("Invalid password");
      }
    } catch (UsernameNotFoundException e) {
      throw new AppException(HttpStatus.NOT_FOUND, username + " not found.");
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
