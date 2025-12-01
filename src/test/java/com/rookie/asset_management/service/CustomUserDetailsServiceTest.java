package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rookie.asset_management.entity.Role;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.entity.UserDetailModel;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.repository.UserRepository;
import com.rookie.asset_management.service.impl.CustomUserDetailsServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private CustomUserDetailsServiceImpl customUserDetailsService;

  private User testUser;

  @BeforeEach
  void setUp() {
    Role role = new Role();
    role.setId(1);
    role.setName("ADMIN");

    testUser = new User();
    testUser.setId(1);
    testUser.setUsername("testuser");
    testUser.setPassword("password123");
    testUser.setRole(role);
    testUser.setDisabled(false);
  }

  @Test
  @DisplayName("loadUserByUsername should return UserDetailModel when user exists")
  void loadUserByUsername_UserExists_ReturnsUserDetailModel() {
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

    UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

    assertNotNull(result);
    assertInstanceOf(UserDetailModel.class, result);
    UserDetailModel userDetailModel = (UserDetailModel) result;
    assertEquals("testuser", userDetailModel.getUsername());
    verify(userRepository).findByUsername("testuser");
  }

  @Test
  @DisplayName("loadUserByUsername should throw AppException when user not found")
  void loadUserByUsername_UserNotFound_ThrowsAppException() {
    when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

    AppException exception =
        assertThrows(
            AppException.class, () -> customUserDetailsService.loadUserByUsername("nonexistent"));

    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatusCode());
    assertTrue(exception.getMessage().contains("nonexistent"));
    assertTrue(exception.getMessage().contains("not found"));
    verify(userRepository).findByUsername("nonexistent");
  }
}
