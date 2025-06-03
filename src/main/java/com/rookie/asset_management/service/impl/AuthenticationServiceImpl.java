package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.dto.request.authentication.ChangePasswordRequestDTO;
import com.rookie.asset_management.dto.request.authentication.FirstLoginChangePasswordRequestDTO;
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

  private void validatePassword(String password, String fieldName) {
    if (password == null || password.trim().isEmpty()) {
      throw new AppException(HttpStatus.BAD_REQUEST, fieldName + " cannot be empty");
    }
    if (password.length() < 8 || password.length() > 128) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, fieldName + " must be between 8 and 128 characters");
    }
    String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,128}$";
    if (!password.matches(passwordPattern)) {
      throw new AppException(
          HttpStatus.BAD_REQUEST,
          fieldName
              + " must contain at least one uppercase letter, one lowercase letter, one number, and one special character (@#$%^&+=!)");
    }
  }

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

      return LoginResponseDTO.builder()
          .role(role)
          .username(userDetails.getUsername())
          .isFirstLogin(isFirstLogin)
          .build();

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

    // Validate oldPassword
    if (changePasswordRequestDTO.getOldPassword() == null
        || changePasswordRequestDTO.getOldPassword().trim().isEmpty()) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Old password cannot be empty");
    }

    // Validate newPassword
    validatePassword(changePasswordRequestDTO.getNewPassword(), "New password");

    // Check if oldPassword matches
    if (!passwordEncoder.matches(changePasswordRequestDTO.getOldPassword(), user.getPassword())) {
      throw new AppException(HttpStatus.CONFLICT, "Incorrect password!");
    }

    // Check if newPassword is different from oldPassword
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

  @Override
  public String firstLoginChangePassword(
      FirstLoginChangePasswordRequestDTO firstLoginChangePasswordRequestDTO,
      HttpServletResponse response) {
    String username = jwtService.extractUsername();
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "User Not Found"));

    // Validate newPassword
    validatePassword(firstLoginChangePasswordRequestDTO.getNewPassword(), "New password");

    // Check if newPassword is different from oldPassword
    if (passwordEncoder.matches(
        firstLoginChangePasswordRequestDTO.getNewPassword(), user.getPassword())) {
      throw new AppException(HttpStatus.CONFLICT, "New password must be different to the old one");
    }

    user.setPassword(passwordEncoder.encode(firstLoginChangePasswordRequestDTO.getNewPassword()));
    if (user.getFirstLogin()) {
      user.setFirstLogin(false);
    }
    userRepository.save(user);
    jwtService.generateToken(username, response);
    return "Password changed successfully!";
  }
}
