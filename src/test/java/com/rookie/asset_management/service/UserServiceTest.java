package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.rookie.asset_management.dto.response.user.UserDetailDtoResponse;
import com.rookie.asset_management.entity.Location;
import com.rookie.asset_management.entity.Role;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.entity.UserProfile;
import com.rookie.asset_management.enums.Gender;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.mapper.UserMapper;
import com.rookie.asset_management.repository.UserRepository;
import com.rookie.asset_management.service.impl.UserServiceImpl;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock private UserRepository userRepository;

  @Mock private UserMapper userMapper;

  @InjectMocks private UserServiceImpl userService;

  @Test
  @DisplayName("getUserDetails should return UserDetailsDtoResponse when user exists")
  void getUserDetails_shouldReturnUserDetailsDto_whenUserExists() {
    // GIVEN
    Integer userId = 1;
    User user = new User();
    user.setId(userId);
    user.setStaffCode("SD1234");
    user.setUsername("nhatnl");
    user.setJoinedDate(LocalDate.of(2023, 5, 1));

    Role role = new Role();
    role.setName("Admin");
    user.setRole(role);

    Location location = new Location();
    location.setName("DN");
    user.setLocation(location);

    UserProfile profile = new UserProfile();
    profile.setFirstName("Nhat");
    profile.setLastName("Nguyen");
    profile.setDob(LocalDate.of(1995, 8, 12));
    profile.setGender(Gender.MALE);
    user.setUserProfile(profile);

    UserDetailDtoResponse expectedDto =
        new UserDetailDtoResponse(
            "SD1234",
            "nhatnl",
            LocalDate.of(2023, 5, 1),
            "DN",
            "Admin",
            "Nhat Nguyen",
            LocalDate.of(1995, 8, 12),
            "Male");

    // WHEN
    Mockito.when(userRepository.findByIdAndDisabledFalse(userId)).thenReturn(Optional.of(user));
    Mockito.when(userMapper.toUserDetailsDto(user)).thenReturn(expectedDto);

    // THEN
    UserDetailDtoResponse result = userService.getUserDetails(userId);
    assertEquals(expectedDto.getStaffCode(), result.getStaffCode());
    assertEquals(expectedDto.getFullName(), result.getFullName());
    assertEquals(expectedDto.getRole(), result.getRole());
  }

  @Test
  @DisplayName("getUserDetails should throw AppException when user not found")
  void getUserDetails_shouldThrowException_whenUserNotFound() {
    int userId = 99;
    Mockito.when(userRepository.findByIdAndDisabledFalse(userId)).thenReturn(Optional.empty());

    AppException exception =
        assertThrows(
            AppException.class,
            () -> {
              userService.getUserDetails(userId);
            });

    assertEquals("User not found", exception.getMessage());
  }
}
