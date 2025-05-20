package com.rookie.asset_management.controller;

import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.user.UserDetailDtoResponse;
import com.rookie.asset_management.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
  UserService userService;

  @GetMapping("/{userId}")
  public ResponseEntity<ApiDtoResponse<UserDetailDtoResponse>> getUserDetails(
      @PathVariable int userId) {
    UserDetailDtoResponse user = userService.getUserDetails(userId);

    ApiDtoResponse<UserDetailDtoResponse> response =
        ApiDtoResponse.<UserDetailDtoResponse>builder().message("Success").data(user).build();
    return ResponseEntity.ok(response);
  }
}
