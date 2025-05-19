package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.rookie.asset_management.dto.request.UserFilterRequest;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.user.UserDetailDtoResponse;
import com.rookie.asset_management.dto.response.user.UserDtoResponse;
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
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock private UserRepository userRepository;

  @Mock private UserMapper userMapper;

  @InjectMocks private UserServiceImpl userService;

  @Test
  @DisplayName("Test getAllUsers with valid request")
  void testGetAllUsers() {
    UserFilterRequest userFilterRequest = UserFilterRequest.builder().build();

    int adminId = 2;

    UserDtoResponse mockUserDtoResponse =
        UserDtoResponse.builder()
            .id(1)
            .fullName("full name of user")
            .staffCode("SD1234")
            .role("Admin")
            .build();

    when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(mockUserDtoResponse)));

    when(userMapper.toPagingResult(any(PageImpl.class), any(Function.class)))
        .thenReturn(new PagingDtoResponse<>(List.of(mockUserDtoResponse), 1, 1, 1, 1, false));

    PagingDtoResponse<UserDtoResponse> result =
        userService.getAllUsers(adminId, userFilterRequest, 0, 10, "id", "asc");

    assertEquals(1, result.getTotalElements());
    assertEquals(
        "full name of user", result.getContent().stream().toList().getFirst().getFullName());
  }

  @Test
  @DisplayName("Test getAllUsers with empty result without exception")
  void testGetAllUsersEmptyResult() {
    UserFilterRequest userFilterRequest = UserFilterRequest.builder().build();

    when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of()));

    when(userMapper.toPagingResult(any(PageImpl.class), any(Function.class)))
        .thenReturn(new PagingDtoResponse<>(List.of(), 0, 0, 0, 0, true));

    PagingDtoResponse<UserDtoResponse> result =
        userService.getAllUsers(1, userFilterRequest, 0, 10, "id", "asc");

    assertEquals(0, result.getTotalElements());
    assertEquals(true, result.getEmpty());
  }

  @Test
  @DisplayName("Test getAllUsers with invalid adminId")
  void testGetAllUsersWithInvalidAdminId() {
    UserFilterRequest userFilterRequest = UserFilterRequest.builder().build();

    AppException exception =
        assertThrows(
            AppException.class,
            () -> userService.getAllUsers(null, userFilterRequest, 0, 10, "id", "asc"));

    assertEquals(
        "Admin ID is required to get user list based on admin location", exception.getMessage());
  }

  @Test
  @DisplayName("test getAllUsers with default sorting with firstName ascending")
  void testGetAllUsersWithDefaultSorting() {
    UserFilterRequest userFilterRequest = UserFilterRequest.builder().build();

    UserDtoResponse first =
        UserDtoResponse.builder().id(1).fullName("A A").staffCode("SD1234").role("Admin").build();

    UserDtoResponse second =
        UserDtoResponse.builder().id(2).fullName("B B").staffCode("SD1234").role("Admin").build();

    UserDtoResponse third =
        UserDtoResponse.builder().id(3).fullName("C C").staffCode("SD1234").role("Admin").build();

    when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(first, second, third)));

    when(userMapper.toPagingResult(any(PageImpl.class), any(Function.class)))
        .thenReturn(new PagingDtoResponse<>(List.of(first, second, third), 1, 3, 1, 1, false));

    PagingDtoResponse<UserDtoResponse> result =
        userService.getAllUsers(1, userFilterRequest, 0, 10, null, null);

    assertEquals(3, result.getTotalElements());
    assertEquals("A A", result.getContent().stream().toList().getFirst().getFullName());
    assertEquals("B B", result.getContent().stream().toList().get(1).getFullName());
    assertEquals("C C", result.getContent().stream().toList().get(2).getFullName());
  }

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
