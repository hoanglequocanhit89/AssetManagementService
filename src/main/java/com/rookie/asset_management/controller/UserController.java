package com.rookie.asset_management.controller;

import com.rookie.asset_management.constant.ApiPaths;
import com.rookie.asset_management.dto.request.UserRequestDTO;
import com.rookie.asset_management.dto.request.user.UpdateUserRequest;
import com.rookie.asset_management.dto.request.user.UserFilterRequest;
import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.user.UserBriefDtoResponse;
import com.rookie.asset_management.dto.response.user.UserDetailDtoResponse;
import com.rookie.asset_management.dto.response.user.UserDtoResponse;
import com.rookie.asset_management.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(ApiPaths.V1 + "/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController extends ApiV1Controller {
  UserService userService;

  @GetMapping("/{userId}")
  public ResponseEntity<ApiDtoResponse<UserDetailDtoResponse>> getUserDetails(
      @PathVariable int userId) {
    UserDetailDtoResponse user = userService.getUserDetails(userId);
    ApiDtoResponse<UserDetailDtoResponse> response =
        ApiDtoResponse.<UserDetailDtoResponse>builder()
            .message("User details retrieved successfully")
            .data(user)
            .build();
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<ApiDtoResponse<PagingDtoResponse<UserDtoResponse>>> getAllUsers(
      @ModelAttribute UserFilterRequest userFilterRequest,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size,
      @RequestParam(defaultValue = "firstName") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {

    PagingDtoResponse<UserDtoResponse> users =
        userService.getAllUsers(userFilterRequest, page, size, sortBy, sortDir);
    ApiDtoResponse<PagingDtoResponse<UserDtoResponse>> response =
        ApiDtoResponse.<PagingDtoResponse<UserDtoResponse>>builder()
            .message("User list retrieved successfully")
            .data(users)
            .build();
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<ApiDtoResponse<UserDetailDtoResponse>> createUser(
      @Valid @RequestBody UserRequestDTO request) {
    UserDetailDtoResponse response = userService.createUser(request);
    ApiDtoResponse<UserDetailDtoResponse> apiResponse =
        ApiDtoResponse.<UserDetailDtoResponse>builder()
            .message("User created successfully")
            .data(response)
            .build();
    return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
  }

  @PutMapping("/{userId}")
  public ResponseEntity<ApiDtoResponse<Void>> updateUser(
      @PathVariable int userId, @Valid @RequestBody UpdateUserRequest request) {
    userService.updateUser(userId, request);
    ApiDtoResponse<Void> response =
        ApiDtoResponse.<Void>builder().message("User updated successfully").build();
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<ApiDtoResponse<Void>> deleteUser(@PathVariable @NotNull int userId) {
    userService.deleteUser(userId);
    ApiDtoResponse<Void> response =
        ApiDtoResponse.<Void>builder().message("User deleted successfully").build();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/all/brief")
  public ResponseEntity<ApiDtoResponse<List<UserBriefDtoResponse>>> getAllUserBrief(
      @RequestParam(defaultValue = "") String query,
      @RequestParam(defaultValue = "firstName") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {

    List<UserBriefDtoResponse> users = userService.getAllUserBrief(query, sortBy, sortDir);
    ApiDtoResponse<List<UserBriefDtoResponse>> response =
        ApiDtoResponse.<List<UserBriefDtoResponse>>builder()
            .message("User list retrieved successfully")
            .data(users)
            .build();
    return ResponseEntity.ok(response);
  }
}
