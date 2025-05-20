package com.rookie.asset_management.controller;

import com.rookie.asset_management.constant.ApiPaths;
import com.rookie.asset_management.dto.request.UserFilterRequest;
import com.rookie.asset_management.dto.request.UserRequestDTO;
import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.user.UserDetailDtoResponse;
import com.rookie.asset_management.dto.response.user.UserDtoResponse;
import com.rookie.asset_management.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
        ApiDtoResponse.<UserDetailDtoResponse>builder().message("Success").data(user).build();
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<ApiDtoResponse<PagingDtoResponse<UserDtoResponse>>> getAllUsers(
      @Valid @RequestParam Integer adminId,
      @ModelAttribute UserFilterRequest userFilterRequest,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size,
      @RequestParam(defaultValue = "firstName") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {

    PagingDtoResponse<UserDtoResponse> users =
        userService.getAllUsers(adminId, userFilterRequest, page, size, sortBy, sortDir);
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
}
