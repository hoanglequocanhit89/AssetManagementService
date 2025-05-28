package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.rookie.asset_management.dto.request.authentication.ChangePasswordRequestDTO;
import com.rookie.asset_management.dto.request.authentication.LoginRequestDTO;
import com.rookie.asset_management.dto.response.authentication.LoginResponseDTO;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.repository.UserRepository;
import com.rookie.asset_management.service.impl.AuthenticationServiceImpl;
import com.rookie.asset_management.util.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

  @Mock private AuthenticationManager authenticationManager;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private UserRepository userRepository;

  @Mock private JwtService jwtService;

  @InjectMocks private AuthenticationServiceImpl authenticationService;

  private User user;
  private HttpServletResponse response;

  @BeforeEach
  void setUp() {
    // Mock user
    user = new User();
    user.setId(1);
    user.setUsername("testuser");
    user.setPassword("encodedPassword");
    user.setFirstLogin(true);
    user.setDisabled(false);

    // Mock response
    response = mock(HttpServletResponse.class);

    // Clear SecurityContextHolder
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Login successfully with valid credentials")
  void login_Successful() {
    // GIVEN
    LoginRequestDTO loginRequestDTO =
        LoginRequestDTO.builder().username("testuser").password("password").build();

    // Use real UserDetails implementation
    UserDetails userDetails =
        new org.springframework.security.core.userdetails.User(
            "testuser", "password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(userDetails);

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);

    // Mock SecurityUtils.isFirstLogin
    try (var mockedStatic = mockStatic(SecurityUtils.class)) {
      mockedStatic.when(SecurityUtils::isFirstLogin).thenReturn(true);

      // WHEN
      LoginResponseDTO result = authenticationService.login(loginRequestDTO, response);

      // THEN
      assertNotNull(result);
      assertEquals("ROLE_ADMIN", result.getRole());
      assertTrue(result.getIsFirstLogin());
      verify(jwtService).generateToken("testuser", response);
      verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
  }

  @Test
  @DisplayName("Login fails due to incorrect password")
  void login_Fail_BadCredentials() {
    // GIVEN
    LoginRequestDTO loginRequestDTO =
        LoginRequestDTO.builder().username("testuser").password("wrongpassword").build();

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Incorrect password"));

    // WHEN & THEN
    AppException exception =
        assertThrows(
            AppException.class, () -> authenticationService.login(loginRequestDTO, response));
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatusCode());
    assertEquals("Incorrect password", exception.getMessage());
    verify(jwtService, never()).generateToken(anyString(), any(HttpServletResponse.class));
  }

  @Test
  @DisplayName("Login fails due to locked account")
  void login_Fail_AccountLocked() {
    // GIVEN
    LoginRequestDTO loginRequestDTO =
        LoginRequestDTO.builder().username("testuser").password("password").build();

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new LockedException("Account is locked"));

    // WHEN & THEN
    AppException exception =
        assertThrows(
            AppException.class, () -> authenticationService.login(loginRequestDTO, response));
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatusCode());
    assertEquals("Account is locked", exception.getMessage());
    verify(jwtService, never()).generateToken(anyString(), any(HttpServletResponse.class));
  }

  @Test
  @DisplayName("Login fails due to disabled account")
  void login_Fail_AccountDisabled() {
    // GIVEN
    LoginRequestDTO loginRequestDTO =
        LoginRequestDTO.builder().username("testuser").password("password").build();

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new DisabledException("Account is disabled"));

    // WHEN & THEN
    AppException exception =
        assertThrows(
            AppException.class, () -> authenticationService.login(loginRequestDTO, response));
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatusCode());
    assertEquals("Account is disabled", exception.getMessage());
    verify(jwtService, never()).generateToken(anyString(), any(HttpServletResponse.class));
  }

  @Test
  @DisplayName("Change password successfully")
  void changePassword_Successful() {
    // GIVEN
    ChangePasswordRequestDTO request =
        ChangePasswordRequestDTO.builder()
            .oldPassword("oldPassword")
            .newPassword("newPassword")
            .build();

    when(jwtService.extractUsername()).thenReturn("testuser");
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
    when(passwordEncoder.matches("newPassword", "encodedPassword")).thenReturn(false);
    when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

    // WHEN
    String result = authenticationService.changePassword(request, response);

    // THEN
    assertEquals("Password changed successfully!", result);
    assertFalse(user.getFirstLogin());
    assertEquals("newEncodedPassword", user.getPassword());
    verify(userRepository).save(user);
  }

  @Test
  @DisplayName("Change password fails due to incorrect old password")
  void changePassword_Fail_IncorrectOldPassword() {
    // GIVEN
    ChangePasswordRequestDTO request =
        ChangePasswordRequestDTO.builder()
            .oldPassword("wrongOldPassword")
            .newPassword("newPassword")
            .build();

    when(jwtService.extractUsername()).thenReturn("testuser");
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("wrongOldPassword", "encodedPassword")).thenReturn(false);

    // WHEN & THEN
    AppException exception =
        assertThrows(
            AppException.class, () -> authenticationService.changePassword(request, response));
    assertEquals(HttpStatus.CONFLICT, exception.getHttpStatusCode());
    assertEquals("Incorrect password!", exception.getMessage());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("Change password fails due to new password being the same as old password")
  void changePassword_Fail_NewPasswordSameAsOld() {
    // GIVEN
    ChangePasswordRequestDTO request =
        ChangePasswordRequestDTO.builder()
            .oldPassword("oldPassword")
            .newPassword("oldPassword")
            .build();

    when(jwtService.extractUsername()).thenReturn("testuser");
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
    when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);

    // WHEN & THEN
    AppException exception =
        assertThrows(
            AppException.class, () -> authenticationService.changePassword(request, response));
    assertEquals(HttpStatus.CONFLICT, exception.getHttpStatusCode());
    assertEquals("New password must be different to the old one", exception.getMessage());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("Change password fails due to user not found")
  void changePassword_Fail_UserNotFound() {
    // GIVEN
    ChangePasswordRequestDTO request =
        ChangePasswordRequestDTO.builder()
            .oldPassword("oldPassword")
            .newPassword("newPassword")
            .build();

    when(jwtService.extractUsername()).thenReturn("testuser");
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

    // WHEN & THEN
    AppException exception =
        assertThrows(
            AppException.class, () -> authenticationService.changePassword(request, response));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
    assertEquals("User Not Found", exception.getMessage());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("Logout successfully")
  void logout_Successful() {
    // WHEN
    authenticationService.logout(response);

    // THEN
    verify(jwtService).removeTokenFromCookie(response);
  }
}
