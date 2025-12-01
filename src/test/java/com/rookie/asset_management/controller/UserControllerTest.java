package com.rookie.asset_management.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.rookie.asset_management.dto.request.UserRequestDTO;
import com.rookie.asset_management.dto.request.user.UpdateUserRequest;
import com.rookie.asset_management.dto.request.user.UserFilterRequest;
import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.user.CreateUserDtoResponse;
import com.rookie.asset_management.dto.response.user.UserBriefDtoResponse;
import com.rookie.asset_management.dto.response.user.UserDetailDtoResponse;
import com.rookie.asset_management.dto.response.user.UserDtoResponse;
import com.rookie.asset_management.enums.Gender;
import com.rookie.asset_management.service.UserService;
import java.time.LocalDate;
import java.util.List;
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
class UserControllerTest {

  @Mock private UserService userService;

  @InjectMocks private UserController userController;

  @Nested
  @DisplayName("getUserDetails Tests")
  class GetUserDetailsTest {

    @Test
    @DisplayName("should return user details successfully")
    void getUserDetails_Success_ReturnsUserDetails() {
      UserDetailDtoResponse detailResponse = new UserDetailDtoResponse();
      detailResponse.setStaffCode("SD0001");
      detailResponse.setFullName("John Doe");

      when(userService.getUserDetails(1)).thenReturn(detailResponse);

      ResponseEntity<ApiDtoResponse<UserDetailDtoResponse>> response =
          userController.getUserDetails(1);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("User details retrieved successfully", response.getBody().getMessage());
      assertEquals("SD0001", response.getBody().getData().getStaffCode());
    }
  }

  @Nested
  @DisplayName("getAllUsers Tests")
  class GetAllUsersTest {

    @Test
    @DisplayName("should return paginated users successfully")
    void getAllUsers_Success_ReturnsPaginatedUsers() {
      UserFilterRequest filterRequest = UserFilterRequest.builder().query("").type("").build();
      PagingDtoResponse<UserDtoResponse> pagingResponse = new PagingDtoResponse<>();
      UserDtoResponse userDto =
          UserDtoResponse.builder()
              .id(1)
              .username("johnd")
              .fullName("John Doe")
              .staffCode("SD0001")
              .joinedDate(LocalDate.of(2024, 1, 15))
              .role("STAFF")
              .canDisable(true)
              .build();
      pagingResponse.setContent(List.of(userDto));

      when(userService.getAllUsers(
              any(UserFilterRequest.class), eq(0), eq(20), eq("firstName"), eq("asc")))
          .thenReturn(pagingResponse);

      ResponseEntity<ApiDtoResponse<PagingDtoResponse<UserDtoResponse>>> response =
          userController.getAllUsers(filterRequest, 0, 20, "firstName", "asc");

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("User list retrieved successfully", response.getBody().getMessage());
      assertNotNull(response.getBody().getData());
    }
  }

  @Nested
  @DisplayName("createUser Tests")
  class CreateUserTest {

    @Test
    @DisplayName("should create user successfully")
    void createUser_Success_ReturnsCreated() {
      UserRequestDTO request = new UserRequestDTO();
      request.setFirstName("John");
      request.setLastName("Doe");
      request.setGender(Gender.MALE);
      request.setEmail("john.doe@example.com");
      request.setDob(LocalDate.of(1990, 1, 1));
      request.setJoinedDate(LocalDate.of(2024, 1, 15));
      request.setType("STAFF");
      request.setLocation("HCM");

      CreateUserDtoResponse serviceResponse = new CreateUserDtoResponse();
      serviceResponse.setStaffCode("SD0001");
      serviceResponse.setUsername("johnd");

      when(userService.createUser(any(UserRequestDTO.class))).thenReturn(serviceResponse);

      ResponseEntity<ApiDtoResponse<CreateUserDtoResponse>> response =
          userController.createUser(request);

      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("User created successfully", response.getBody().getMessage());
      assertEquals("SD0001", response.getBody().getData().getStaffCode());
    }
  }

  @Nested
  @DisplayName("updateUser Tests")
  class UpdateUserTest {

    @Test
    @DisplayName("should update user successfully")
    void updateUser_Success_ReturnsOk() {
      UpdateUserRequest request =
          UpdateUserRequest.builder()
              .dob(LocalDate.of(1990, 1, 1))
              .gender(Gender.MALE)
              .joinedDate(LocalDate.of(2024, 1, 15))
              .role("STAFF")
              .build();

      doNothing().when(userService).updateUser(eq(1), any(UpdateUserRequest.class));

      ResponseEntity<ApiDtoResponse<Void>> response = userController.updateUser(1, request);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("User updated successfully", response.getBody().getMessage());
      verify(userService).updateUser(eq(1), any(UpdateUserRequest.class));
    }
  }

  @Nested
  @DisplayName("deleteUser Tests")
  class DeleteUserTest {

    @Test
    @DisplayName("should delete user successfully")
    void deleteUser_Success_ReturnsOk() {
      doNothing().when(userService).deleteUser(1);

      ResponseEntity<ApiDtoResponse<Void>> response = userController.deleteUser(1);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("User deleted successfully", response.getBody().getMessage());
      verify(userService).deleteUser(1);
    }
  }

  @Nested
  @DisplayName("getAllUserBrief Tests")
  class GetAllUserBriefTest {

    @Test
    @DisplayName("should return user briefs successfully")
    void getAllUserBrief_Success_ReturnsUserBriefs() {
      UserBriefDtoResponse brief = new UserBriefDtoResponse(1, "SD0001", "John Doe", "STAFF");

      when(userService.getAllUserBrief("john", "firstName", "asc")).thenReturn(List.of(brief));

      ResponseEntity<ApiDtoResponse<List<UserBriefDtoResponse>>> response =
          userController.getAllUserBrief("john", "firstName", "asc");

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("User list retrieved successfully", response.getBody().getMessage());
      assertEquals(1, response.getBody().getData().size());
    }
  }
}
