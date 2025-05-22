package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.rookie.asset_management.dto.request.UserRequestDTO;
import com.rookie.asset_management.dto.request.user.UpdateUserRequest;
import com.rookie.asset_management.dto.request.user.UserFilterRequest;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.user.UserDetailDtoResponse;
import com.rookie.asset_management.dto.response.user.UserDtoResponse;
import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.Location;
import com.rookie.asset_management.entity.ReturningRequest;
import com.rookie.asset_management.entity.Role;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.entity.UserProfile;
import com.rookie.asset_management.enums.AssignmentStatus;
import com.rookie.asset_management.enums.Gender;
import com.rookie.asset_management.enums.ReturningRequestStatus;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.mapper.UserMapper;
import com.rookie.asset_management.repository.RoleRepository;
import com.rookie.asset_management.repository.UserRepository;
import com.rookie.asset_management.service.impl.UserServiceImpl;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock private UserRepository userRepository;

  @Mock private RoleRepository roleRepository;

  @Mock private UserMapper userMapper;

  @InjectMocks private UserServiceImpl userService;

  private User adminUser;

  @BeforeEach
  void setUp() {
    // Mock admin user (ID = 1, role = ADMIN, location = HCM)
    adminUser = new User();
    adminUser.setId(1);
    adminUser.setUsername("admin1");
    Role adminRole = new Role();
    adminRole.setName("ADMIN");
    adminUser.setRole(adminRole);
    Location adminLocation = new Location();
    adminLocation.setId(1);
    adminLocation.setName("HCM");
    adminUser.setLocation(adminLocation);
    adminUser.setDisabled(false);
  }

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
            1,
            "SD1234",
            "nhatnl",
            LocalDate.of(2023, 5, 1),
            "DN",
            "Admin",
            "Nhat",
            "Nguyen Lam",
            "Nhat Nguyen Lam",
            LocalDate.of(1995, 8, 12),
            "Male");
    // WHEN
    when(userRepository.findByIdAndDisabledFalse(userId)).thenReturn(Optional.of(user));
    when(userMapper.toUserDetailsDto(user)).thenReturn(expectedDto);
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
    when(userRepository.findByIdAndDisabledFalse(userId)).thenReturn(Optional.empty());
    AppException exception =
        assertThrows(
            AppException.class,
            () -> {
              userService.getUserDetails(userId);
            });
    assertEquals("User not found", exception.getMessage());
  }

  @Test
  @DisplayName("Update user successfully")
  void updateUser_SuccessfulUpdate() {
    // GIVEN
    int userId = 1;
    UpdateUserRequest request =
        UpdateUserRequest.builder()
            .dob(LocalDate.of(2000, 1, 1))
            .joinedDate(LocalDate.of(2025, 1, 1))
            .gender(Gender.MALE)
            .role("STAFF")
            .build();

    User user = new User();
    Role role = new Role();
    role.setName("ADMIN");
    user.setId(1);
    user.setRole(role);
    user.setJoinedDate(LocalDate.of(2024, 10, 10));
    UserProfile profile = new UserProfile();
    profile.setDob(LocalDate.of(1990, 1, 1));
    user.setUserProfile(profile);

    // WHEN
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(roleRepository.findByName("STAFF")).thenReturn(role);

    userService.updateUser(userId, request);

    // THEN
    verify(userMapper).updateUserFromDto(request, user);
    verify(userMapper).updateUserProfileFromDto(request, profile);
    verify(userRepository).save(user);

    assertEquals(role, user.getRole());
  }

  @Test
  void updateUser_UserNotFound_ShouldThrowException() {
    when(userRepository.findById(1)).thenReturn(Optional.empty());

    AppException ex =
        assertThrows(
            AppException.class,
            () -> {
              userService.updateUser(1, UpdateUserRequest.builder().build());
            });

    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatusCode());
    assertEquals("User not found", ex.getMessage());
  }

  @Test
  void updateUser_RoleNotFound_ShouldThrowException() {
    int userId = 2;
    UpdateUserRequest request = UpdateUserRequest.builder().build();
    request.setRole("NON_EXISTENT_ROLE");

    User user = new User();
    user.setUserProfile(new UserProfile());

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(roleRepository.findByName("NON_EXISTENT_ROLE")).thenReturn(null);

    AppException ex =
        assertThrows(AppException.class, () -> userService.updateUser(userId, request));

    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatusCode());
    assertEquals("Role not found", ex.getMessage());
  }

  @Test
  void updateUser_JoinedDateBeforeDob_ShouldThrowException() {
    int userId = 3;
    UpdateUserRequest request = UpdateUserRequest.builder().build();

    LocalDate dob = LocalDate.of(2000, 1, 1);
    LocalDate joinedDate = LocalDate.of(1999, 12, 31);

    User user = new User();
    user.setJoinedDate(joinedDate);

    UserProfile profile = new UserProfile();
    profile.setDob(dob);
    user.setUserProfile(profile);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    AppException ex =
        assertThrows(AppException.class, () -> userService.updateUser(userId, request));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatusCode());
    assertEquals("Joined date must be after date of birth.", ex.getMessage());
  }

  // Test cases for createUser
  @Test
  @DisplayName("Create user successfully with anh nguyen van → anhnv")
  void createUser_Successful_AnhNguyenVan() {
    // GIVEN
    UserRequestDTO request = new UserRequestDTO();
    request.setFirstName("anh");
    request.setLastName("nguyen van");
    request.setGender(Gender.MALE);
    request.setDob(LocalDate.of(1995, 1, 1));
    request.setJoinedDate(LocalDate.of(2025, 5, 21));
    request.setType("Staff");
    request.setAdminId(1); // Thêm adminId

    User user = new User();
    UserProfile profile = new UserProfile();
    profile.setFirstName("anh");
    profile.setLastName("nguyen van");
    profile.setDob(LocalDate.of(1995, 1, 1));
    profile.setGender(Gender.MALE);
    user.setUserProfile(profile);
    user.setJoinedDate(LocalDate.of(2025, 5, 21));
    user.setId(1);
    user.setLocation(adminUser.getLocation()); // Location from admin for Staff
    Role staffRole = new Role();
    staffRole.setName("STAFF");
    user.setRole(staffRole);

    UserDetailDtoResponse responseDto =
        new UserDetailDtoResponse(
            1,
            "SD0001",
            "anhnv",
            LocalDate.of(2025, 5, 21),
            "HCM",
            "Staff",
            "anh",
            "nguyen van",
            "anh nguyen van",
            LocalDate.of(1995, 1, 1),
            "Male");

    when(userRepository.findByIdAndDisabledFalse(1)).thenReturn(Optional.of(adminUser));
    when(userMapper.toEntity(request)).thenReturn(user);
    when(userRepository.existsByUsername("anhnv")).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(userMapper.toUserDetailsDto(any(User.class))).thenReturn(responseDto);

    // WHEN
    UserDetailDtoResponse result = userService.createUser(request, request.getAdminId());

    // THEN
    assertEquals("SD0001", result.getStaffCode());
    assertEquals("anhnv", result.getUsername());
    assertEquals("HCM", result.getLocation());
    assertEquals("Staff", result.getRole());
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("Create user successfully with le thi bich ngoc → letbn")
  void createUser_Successful_LeThiBichNgoc() {
    // GIVEN
    UserRequestDTO request = new UserRequestDTO();
    request.setFirstName("le");
    request.setLastName("thi bich ngoc");
    request.setGender(Gender.FEMALE);
    request.setDob(LocalDate.of(1995, 1, 1));
    request.setJoinedDate(LocalDate.of(2025, 5, 21));
    request.setType("Staff");
    request.setAdminId(1); // Thêm adminId

    User user = new User();
    UserProfile profile = new UserProfile();
    profile.setFirstName("le");
    profile.setLastName("thi bich ngoc");
    profile.setDob(LocalDate.of(1995, 1, 1));
    profile.setGender(Gender.FEMALE);
    user.setUserProfile(profile);
    user.setJoinedDate(LocalDate.of(2025, 5, 21));
    user.setId(2);
    user.setLocation(adminUser.getLocation()); // Location from admin for Staff
    Role staffRole = new Role();
    staffRole.setName("STAFF");
    user.setRole(staffRole);

    UserDetailDtoResponse responseDto =
        new UserDetailDtoResponse(
            1,
            "SD0002",
            "letbn",
            LocalDate.of(2025, 5, 21),
            "HCM",
            "Staff",
            "ngoc",
            "le thi bich",
            "le thi bich ngoc",
            LocalDate.of(1995, 1, 1),
            "Female");

    when(userRepository.findByIdAndDisabledFalse(1)).thenReturn(Optional.of(adminUser));
    when(userMapper.toEntity(request)).thenReturn(user);
    when(userRepository.existsByUsername("letbn")).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(userMapper.toUserDetailsDto(any(User.class))).thenReturn(responseDto);

    // WHEN
    UserDetailDtoResponse result = userService.createUser(request, request.getAdminId());

    // THEN
    assertEquals("SD0002", result.getStaffCode());
    assertEquals("letbn", result.getUsername());
    assertEquals("HCM", result.getLocation());
    assertEquals("Staff", result.getRole());
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("Create user successfully with tran minh → tranm")
  void createUser_Successful_TranMinh() {
    // GIVEN
    UserRequestDTO request = new UserRequestDTO();
    request.setFirstName("tran");
    request.setLastName("minh");
    request.setGender(Gender.MALE);
    request.setDob(LocalDate.of(1995, 1, 1));
    request.setJoinedDate(LocalDate.of(2025, 5, 21));
    request.setType("Staff");
    request.setAdminId(1); // Thêm adminId

    User user = new User();
    UserProfile profile = new UserProfile();
    profile.setFirstName("tran");
    profile.setLastName("minh");
    profile.setDob(LocalDate.of(1995, 1, 1));
    profile.setGender(Gender.MALE);
    user.setUserProfile(profile);
    user.setJoinedDate(LocalDate.of(2025, 5, 21));
    user.setId(3);
    user.setLocation(adminUser.getLocation()); // Location from admin for Staff
    Role staffRole = new Role();
    staffRole.setName("STAFF");
    user.setRole(staffRole);

    UserDetailDtoResponse responseDto =
        new UserDetailDtoResponse(
            1,
            "SD0003",
            "tranm",
            LocalDate.of(2025, 5, 21),
            "HCM",
            "Staff",
            "minh",
            "tran",
            "tran minh",
            LocalDate.of(1995, 1, 1),
            "Male");

    when(userRepository.findByIdAndDisabledFalse(1)).thenReturn(Optional.of(adminUser));
    when(userMapper.toEntity(request)).thenReturn(user);
    when(userRepository.existsByUsername("tranm")).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(userMapper.toUserDetailsDto(any(User.class))).thenReturn(responseDto);

    // WHEN
    UserDetailDtoResponse result = userService.createUser(request, request.getAdminId());

    // THEN
    assertEquals("SD0003", result.getStaffCode());
    assertEquals("tranm", result.getUsername());
    assertEquals("HCM", result.getLocation());
    assertEquals("Staff", result.getRole());
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("Create user with duplicate usernames - anh nguyen van → anhnv, anhnv1, anhnv2")
  void createUser_DuplicateUsername_ShouldAppendNumber() {
    // GIVEN
    UserRequestDTO request1 = new UserRequestDTO();
    request1.setFirstName("anh");
    request1.setLastName("nguyen van");
    request1.setGender(Gender.MALE);
    request1.setDob(LocalDate.of(1995, 1, 1));
    request1.setJoinedDate(LocalDate.of(2025, 5, 21));
    request1.setType("Staff");
    request1.setAdminId(1); // Thêm adminId

    User user1 = new User();
    UserProfile profile1 = new UserProfile();
    profile1.setFirstName("anh");
    profile1.setLastName("nguyen van");
    profile1.setDob(LocalDate.of(1995, 1, 1));
    profile1.setGender(Gender.MALE);
    user1.setUserProfile(profile1);
    user1.setJoinedDate(LocalDate.of(2025, 5, 21));
    user1.setId(1);
    user1.setLocation(adminUser.getLocation()); // Location from admin
    Role staffRole = new Role();
    staffRole.setName("STAFF");
    user1.setRole(staffRole);

    UserDetailDtoResponse responseDto1 =
        new UserDetailDtoResponse(
            1,
            "SD0001",
            "anhnv",
            LocalDate.of(2025, 5, 21),
            "HCM",
            "Staff",
            "anh",
            "nguyen van",
            "anh nguyen van",
            LocalDate.of(1995, 1, 1),
            "Male");

    // Mock for user 1
    when(userRepository.findByIdAndDisabledFalse(1)).thenReturn(Optional.of(adminUser));
    when(userMapper.toEntity(request1)).thenReturn(user1);
    when(userRepository.existsByUsername("anhnv")).thenReturn(false);
    when(userRepository.save(user1)).thenReturn(user1);
    when(userMapper.toUserDetailsDto(user1)).thenReturn(responseDto1);

    // WHEN
    UserDetailDtoResponse result1 = userService.createUser(request1, request1.getAdminId());

    // THEN
    assertEquals("anhnv", result1.getUsername());
    verify(userRepository).save(user1);

    // Reset mocks for user 2
    reset(userRepository);
    UserRequestDTO request2 = new UserRequestDTO();
    request2.setFirstName("anh");
    request2.setLastName("nguyen van");
    request2.setGender(Gender.MALE);
    request2.setDob(LocalDate.of(1995, 1, 1));
    request2.setJoinedDate(LocalDate.of(2025, 5, 21));
    request2.setType("Staff");
    request2.setAdminId(1); // Thêm adminId

    User user2 = new User();
    UserProfile profile2 = new UserProfile();
    profile2.setFirstName("anh");
    profile2.setLastName("nguyen van");
    profile2.setDob(LocalDate.of(1995, 1, 1));
    profile2.setGender(Gender.MALE);
    user2.setUserProfile(profile2);
    user2.setJoinedDate(LocalDate.of(2025, 5, 21));
    user2.setId(2);
    user2.setLocation(adminUser.getLocation()); // Location from admin
    user2.setRole(staffRole);

    UserDetailDtoResponse responseDto2 =
        new UserDetailDtoResponse(
            1,
            "SD0002",
            "anhnv1",
            LocalDate.of(2025, 5, 21),
            "HCM",
            "Staff",
            "anh",
            "nguyen van",
            "anh nguyen van",
            LocalDate.of(1995, 1, 1),
            "Male");

    // Mock for user 2
    when(userRepository.findByIdAndDisabledFalse(1)).thenReturn(Optional.of(adminUser));
    when(userMapper.toEntity(request2)).thenReturn(user2);
    when(userRepository.existsByUsername("anhnv")).thenReturn(true);
    when(userRepository.existsByUsername("anhnv1")).thenReturn(false);
    when(userRepository.save(user2)).thenReturn(user2);
    when(userMapper.toUserDetailsDto(user2)).thenReturn(responseDto2);

    // WHEN
    UserDetailDtoResponse result2 = userService.createUser(request2, request2.getAdminId());

    // THEN
    assertEquals("anhnv1", result2.getUsername());
    verify(userRepository).save(user2);

    // Reset mocks for user 3
    reset(userRepository);
    UserRequestDTO request3 = new UserRequestDTO();
    request3.setFirstName("anh");
    request3.setLastName("nguyen van");
    request3.setGender(Gender.MALE);
    request3.setDob(LocalDate.of(1995, 1, 1));
    request3.setJoinedDate(LocalDate.of(2025, 5, 21));
    request3.setType("Staff");
    request3.setAdminId(1); // Thêm adminId

    User user3 = new User();
    UserProfile profile3 = new UserProfile();
    profile3.setFirstName("anh");
    profile3.setLastName("nguyen van");
    profile3.setDob(LocalDate.of(1995, 1, 1));
    profile3.setGender(Gender.MALE);
    user3.setUserProfile(profile3);
    user3.setJoinedDate(LocalDate.of(2025, 5, 21));
    user3.setId(3);
    user3.setLocation(adminUser.getLocation()); // Location from admin
    user3.setRole(staffRole);

    UserDetailDtoResponse responseDto3 =
        new UserDetailDtoResponse(
            1,
            "SD0003",
            "anhnv2",
            LocalDate.of(2025, 5, 21),
            "HCM",
            "Staff",
            "anh",
            "nguyen van",
            "anh nguyen van",
            LocalDate.of(1995, 1, 1),
            "Male");

    // Mock for user 3
    when(userRepository.findByIdAndDisabledFalse(1)).thenReturn(Optional.of(adminUser));
    when(userMapper.toEntity(request3)).thenReturn(user3);
    when(userRepository.existsByUsername("anhnv")).thenReturn(true);
    when(userRepository.existsByUsername("anhnv1")).thenReturn(true);
    when(userRepository.existsByUsername("anhnv2")).thenReturn(false);
    when(userRepository.save(user3)).thenReturn(user3);
    when(userMapper.toUserDetailsDto(user3)).thenReturn(responseDto3);

    // WHEN
    UserDetailDtoResponse result3 = userService.createUser(request3, request3.getAdminId());

    // THEN
    assertEquals("anhnv2", result3.getUsername());
    verify(userRepository).save(user3);
  }

  @Test
  @DisplayName("Create user with type Admin should set role correctly")
  void createUser_WithAdminType_ShouldSetRoleCorrectly() {
    // GIVEN
    UserRequestDTO request = new UserRequestDTO();
    request.setFirstName("anh");
    request.setLastName("nguyen van");
    request.setGender(Gender.MALE);
    request.setDob(LocalDate.of(1995, 1, 1));
    request.setJoinedDate(LocalDate.of(2025, 5, 21));
    request.setType("Admin");
    request.setLocation("HN"); // Custom location for Admin
    request.setAdminId(1); // Thêm adminId

    User user = new User();
    UserProfile profile = new UserProfile();
    profile.setFirstName("anh");
    profile.setLastName("nguyen van");
    profile.setDob(LocalDate.of(1995, 1, 1));
    profile.setGender(Gender.MALE);
    user.setUserProfile(profile);
    user.setJoinedDate(LocalDate.of(2025, 5, 21));
    user.setId(1);
    Location customLocation = new Location();
    customLocation.setId(2);
    customLocation.setName("HN");
    user.setLocation(customLocation); // Location from request
    Role adminRole = new Role();
    adminRole.setName("ADMIN");
    user.setRole(adminRole);

    UserDetailDtoResponse responseDto =
        new UserDetailDtoResponse(
            1,
            "SD0001",
            "anhnv",
            LocalDate.of(2025, 5, 21),
            "HCM",
            "Admin",
            "anh",
            "nguyen van",
            "anh nguyen van",
            LocalDate.of(1995, 1, 1),
            "Male");

    when(userRepository.findByIdAndDisabledFalse(1)).thenReturn(Optional.of(adminUser));
    when(userMapper.toEntity(request)).thenReturn(user);
    when(userRepository.existsByUsername("anhnv")).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(userMapper.toUserDetailsDto(any(User.class))).thenReturn(responseDto);

    // WHEN
    UserDetailDtoResponse result = userService.createUser(request, request.getAdminId());

    // THEN
    assertEquals("Admin", result.getRole());
    assertEquals("HCM", result.getLocation());
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("Create user with type Staff should set role correctly")
  void createUser_WithStaffType_ShouldSetRoleCorrectly() {
    // GIVEN
    UserRequestDTO request = new UserRequestDTO();
    request.setFirstName("anh");
    request.setLastName("nguyen van");
    request.setGender(Gender.MALE);
    request.setDob(LocalDate.of(1995, 1, 1));
    request.setJoinedDate(LocalDate.of(2025, 5, 21));
    request.setType("Staff");
    request.setAdminId(1); // Thêm adminId

    User user = new User();
    UserProfile profile = new UserProfile();
    profile.setFirstName("anh");
    profile.setLastName("nguyen van");
    profile.setDob(LocalDate.of(1995, 1, 1));
    profile.setGender(Gender.MALE);
    user.setUserProfile(profile);
    user.setJoinedDate(LocalDate.of(2025, 5, 21));
    user.setId(1);
    user.setLocation(adminUser.getLocation()); // Location from admin
    Role staffRole = new Role();
    staffRole.setName("STAFF");
    user.setRole(staffRole);

    UserDetailDtoResponse responseDto =
        new UserDetailDtoResponse(
            1,
            "SD0001",
            "anhnv",
            LocalDate.of(2025, 5, 21),
            "HCM",
            "Staff",
            "anh",
            "nguyen van",
            "anh nguyen van",
            LocalDate.of(1995, 1, 1),
            "Male");

    when(userRepository.findByIdAndDisabledFalse(1)).thenReturn(Optional.of(adminUser));
    when(userMapper.toEntity(request)).thenReturn(user);
    when(userRepository.existsByUsername("anhnv")).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(userMapper.toUserDetailsDto(any(User.class))).thenReturn(responseDto);

    // WHEN
    UserDetailDtoResponse result = userService.createUser(request, request.getAdminId());

    // THEN
    assertEquals("Staff", result.getRole());
    assertEquals("HCM", result.getLocation());
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("Create user should set default values for disabled and firstLogin")
  void createUser_ShouldSetDefaultValues() {
    // GIVEN
    UserRequestDTO request = new UserRequestDTO();
    request.setFirstName("anh");
    request.setLastName("nguyen van");
    request.setGender(Gender.MALE);
    request.setDob(LocalDate.of(1995, 1, 1));
    request.setJoinedDate(LocalDate.of(2025, 5, 21));
    request.setType("Staff");
    request.setAdminId(1); // Thêm adminId

    User user = new User();
    UserProfile profile = new UserProfile();
    profile.setFirstName("anh");
    profile.setLastName("nguyen van");
    profile.setDob(LocalDate.of(1995, 1, 1));
    profile.setGender(Gender.MALE);
    user.setUserProfile(profile);
    user.setJoinedDate(LocalDate.of(2025, 5, 21));
    user.setId(1);
    user.setLocation(adminUser.getLocation()); // Location from admin
    Role staffRole = new Role();
    staffRole.setName("STAFF");
    user.setRole(staffRole);

    UserDetailDtoResponse responseDto =
        new UserDetailDtoResponse(
            1,
            "SD0001",
            "anhnv",
            LocalDate.of(2025, 5, 21),
            "HCM",
            "Staff",
            "anh",
            "nguyen van",
            "anh nguyen van",
            LocalDate.of(1995, 1, 1),
            "Male");

    when(userRepository.findByIdAndDisabledFalse(1)).thenReturn(Optional.of(adminUser));
    when(userMapper.toEntity(any(UserRequestDTO.class))).thenReturn(user);
    when(userRepository.existsByUsername("anhnv")).thenReturn(false);
    when(userRepository.save(any(User.class)))
        .thenAnswer(
            invocation -> {
              User savedUser = invocation.getArgument(0);
              savedUser.setDisabled(false);
              savedUser.setFirstLogin(true);
              return savedUser;
            });
    when(userMapper.toUserDetailsDto(any(User.class))).thenReturn(responseDto);

    // WHEN
    userService.createUser(request, request.getAdminId());

    // THEN
    verify(userRepository).save(any(User.class));
    assertEquals(false, user.getDisabled());
    assertEquals(true, user.getFirstLogin());
  }

  @Test
  @DisplayName("deleteUser should delete user successfully")
  void deleteUser_shouldDeleteUserSuccessfully() {
    int userId = 1;
    User user = new User();
    user.setId(userId);
    user.setDisabled(false);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    userService.deleteUser(userId);
    verify(userRepository).save(user);
  }

  @Test
  @DisplayName("deleteUser should throw AppException when user not found")
  void deleteUser_shouldThrowException_whenUserNotFound() {
    int userId = 99;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());
    AppException exception =
        assertThrows(
            AppException.class,
            () -> {
              userService.deleteUser(userId);
            });
    assertEquals("User not found", exception.getMessage());
  }

  @Test
  @DisplayName("deleteUser should throw AppException when user is already disabled")
  void deleteUser_shouldThrowException_whenUserAlreadyDisabled() {
    int userId = 1;
    User user = new User();
    user.setId(userId);
    user.setDisabled(true);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    AppException exception =
        assertThrows(
            AppException.class,
            () -> {
              userService.deleteUser(userId);
            });
    assertEquals("User is already disabled", exception.getMessage());
  }

  @Test
  @DisplayName(
      "deleteUser should throw AppException when user has accepted assignments without returning requests")
  void deleteUser_shouldThrowException_whenUserHasAcceptedAssignmentsWithoutReturningRequests() {
    int userId = 1;
    User user = new User();
    user.setId(userId);
    user.setDisabled(false);

    Assignment assignment = new Assignment();
    assignment.setAssignedTo(user);
    assignment.setStatus(AssignmentStatus.ACCEPTED);
    // No returning request set

    user.setAssignments(List.of(assignment));

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    AppException exception = assertThrows(AppException.class, () -> userService.deleteUser(userId));

    assertEquals("User has not returned the asset yet", exception.getMessage());
  }

  @Test
  @DisplayName(
      "Should throw AppException when user has accepted assignments with incomplete returning requests")
  void
      deleteUser_shouldThrowException_whenUserHasAcceptedAssignmentsWithIncompleteReturningRequests() {
    int userId = 1;
    User user = new User();
    user.setId(userId);
    user.setDisabled(false);

    Assignment assignment = new Assignment();
    assignment.setAssignedTo(user);
    assignment.setStatus(AssignmentStatus.ACCEPTED);

    ReturningRequest returningRequest = new ReturningRequest();
    returningRequest.setStatus(ReturningRequestStatus.WAITING);
    assignment.setReturningRequest(returningRequest);

    user.setAssignments(List.of(assignment));

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    AppException exception = assertThrows(AppException.class, () -> userService.deleteUser(userId));

    assertEquals(
        "User has pending returning requests, cannot be deleted, please cancel the request first",
        exception.getMessage());
  }

  @Test
  @DisplayName("Should delete user successfully when all assignments are returned and completed")
  void deleteUser_shouldDeleteUserSuccessfully_whenAllAssignmentsAreReturnedAndCompleted() {
    int userId = 1;
    User user = new User();
    user.setId(userId);
    user.setDisabled(false);

    Assignment assignment = new Assignment();
    assignment.setAssignedTo(user);
    assignment.setStatus(AssignmentStatus.ACCEPTED);

    ReturningRequest returningRequest = new ReturningRequest();
    returningRequest.setStatus(ReturningRequestStatus.COMPLETED);
    assignment.setReturningRequest(returningRequest);

    user.setAssignments(List.of(assignment));

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    userService.deleteUser(userId);

    verify(userRepository).save(user);
  }
}
