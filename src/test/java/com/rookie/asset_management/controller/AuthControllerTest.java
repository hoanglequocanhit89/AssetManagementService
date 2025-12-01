package com.rookie.asset_management.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.rookie.asset_management.dto.request.authentication.ChangePasswordRequestDTO;
import com.rookie.asset_management.dto.request.authentication.FirstLoginChangePasswordRequestDTO;
import com.rookie.asset_management.dto.request.authentication.LoginRequestDTO;
import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.authentication.LoginResponseDTO;
import com.rookie.asset_management.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  @Mock private AuthenticationService authenticationService;

  @Mock private HttpServletResponse httpServletResponse;

  @InjectMocks private AuthController authController;

  @Nested
  @DisplayName("login Tests")
  class LoginTest {

    @Test
    @DisplayName("should login successfully")
    void login_Success_ReturnsLoginResponse() {
      LoginRequestDTO request =
          LoginRequestDTO.builder().username("testuser").password("password123").build();

      LoginResponseDTO loginResponse =
          LoginResponseDTO.builder().username("testuser").role("ADMIN").isFirstLogin(false).build();

      when(authenticationService.login(any(LoginRequestDTO.class), any(HttpServletResponse.class)))
          .thenReturn(loginResponse);

      ResponseEntity<ApiDtoResponse<LoginResponseDTO>> response =
          authController.login(request, httpServletResponse);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Login successfully!", response.getBody().getMessage());
      assertEquals("testuser", response.getBody().getData().getUsername());
      verify(authenticationService)
          .login(any(LoginRequestDTO.class), any(HttpServletResponse.class));
    }
  }

  @Nested
  @DisplayName("changePassword Tests")
  class ChangePasswordTest {

    @Test
    @DisplayName("should change password successfully")
    void changePassword_Success_ReturnsOk() {
      ChangePasswordRequestDTO request =
          ChangePasswordRequestDTO.builder()
              .oldPassword("oldPassword")
              .newPassword("NewPassword123!")
              .build();

      when(authenticationService.changePassword(
              any(ChangePasswordRequestDTO.class), any(HttpServletResponse.class)))
          .thenReturn("Password changed successfully");

      ResponseEntity<ApiDtoResponse<Void>> response =
          authController.changePassword(request, httpServletResponse);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Password changed successfully", response.getBody().getMessage());
      verify(authenticationService)
          .changePassword(any(ChangePasswordRequestDTO.class), any(HttpServletResponse.class));
    }
  }

  @Nested
  @DisplayName("logout Tests")
  class LogoutTest {

    @Test
    @DisplayName("should logout successfully")
    void logout_Success_ReturnsOk() {
      doNothing().when(authenticationService).logout(any(HttpServletResponse.class));

      ResponseEntity<ApiDtoResponse<Void>> response = authController.logout(httpServletResponse);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Logout successfully", response.getBody().getMessage());
      verify(authenticationService).logout(any(HttpServletResponse.class));
    }
  }

  @Nested
  @DisplayName("firstLoginChangePassword Tests")
  class FirstLoginChangePasswordTest {

    @Test
    @DisplayName("should change password on first login successfully")
    void firstLoginChangePassword_Success_ReturnsOk() {
      FirstLoginChangePasswordRequestDTO request =
          FirstLoginChangePasswordRequestDTO.builder().newPassword("NewPassword123!").build();

      when(authenticationService.firstLoginChangePassword(
              any(FirstLoginChangePasswordRequestDTO.class), any(HttpServletResponse.class)))
          .thenReturn("Password changed successfully");

      ResponseEntity<ApiDtoResponse<Void>> response =
          authController.firstLoginChangePassword(request, httpServletResponse);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Password changed successfully", response.getBody().getMessage());
      verify(authenticationService)
          .firstLoginChangePassword(
              any(FirstLoginChangePasswordRequestDTO.class), any(HttpServletResponse.class));
    }
  }
}
