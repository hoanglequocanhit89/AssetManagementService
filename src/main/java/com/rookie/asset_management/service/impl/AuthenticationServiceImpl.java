package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.dto.request.authentication.ChangePasswordRequestDTO;
import com.rookie.asset_management.dto.request.authentication.LoginRequestDTO;
import com.rookie.asset_management.dto.response.authentication.LoginResponseDTO;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.repository.UserRepository;
import com.rookie.asset_management.service.AuthenticationService;
import com.rookie.asset_management.service.JwtService;
import com.rookie.asset_management.util.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

  AuthenticationManager authenticationManager;
  PasswordEncoder passwordEncoder;
  UserRepository userRepository;
  JwtService jwtService;

  @Override
  public LoginResponseDTO login(LoginRequestDTO loginRequestDTO, HttpServletResponse response) {

    try {

      Authentication authenticationRequest =
          UsernamePasswordAuthenticationToken.unauthenticated(
              loginRequestDTO.getUsername(), loginRequestDTO.getPassword());

      Authentication authenticationResponse =
          authenticationManager.authenticate(authenticationRequest);

      SecurityContextHolder.getContext().setAuthentication(authenticationResponse);

      jwtService.generateToken(loginRequestDTO.getUsername(), response);

      UserDetails userDetails = (UserDetails) authenticationResponse.getPrincipal();

      String role =
          userDetails.getAuthorities().stream()
              .findFirst()
              .map(GrantedAuthority::getAuthority)
              .orElseThrow(
                  () ->
                      new AppException(HttpStatus.INTERNAL_SERVER_ERROR, "No role found for user"));

      Boolean isFirstLogin = SecurityUtils.isFirstLogin();

      return LoginResponseDTO.builder().role(role).isFirstLogin(isFirstLogin).build();

    } catch (BadCredentialsException e) {
      throw new AppException(HttpStatus.UNAUTHORIZED, "Incorrect password");
    } catch (DisabledException e) {
      throw new AppException(HttpStatus.UNAUTHORIZED, "Account is disabled");
    } catch (LockedException e) {
      throw new AppException(HttpStatus.UNAUTHORIZED, "Account is locked");
    } catch (AuthenticationException e) {
      throw new AppException(HttpStatus.UNAUTHORIZED, "Authentication failed: " + e.getMessage());
    }
  }

  @Override
  public String changePassword(
      ChangePasswordRequestDTO changePasswordRequestDTO, HttpServletResponse response) {
    String username = jwtService.extractUsername();
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "User Not Found"));
    if (!passwordEncoder.matches(changePasswordRequestDTO.getOldPassword(), user.getPassword())) {
      throw new AppException(HttpStatus.CONFLICT, "Incorrect password!");
    }
    if (passwordEncoder.matches(changePasswordRequestDTO.getNewPassword(), user.getPassword())) {
      throw new AppException(HttpStatus.CONFLICT, "New password must be different to the old one");
    }
    user.setPassword(passwordEncoder.encode(changePasswordRequestDTO.getNewPassword()));
    if (user.getFirstLogin()) {
      user.setFirstLogin(false);
    }
    userRepository.save(user);
    return "Password changed successfully!";
  }

  @Override
  public void logout(HttpServletResponse response) {
    jwtService.removeTokenFromCookie(response);
  }
}
