package com.rookie.asset_management.controller;

import com.rookie.asset_management.dto.request.UserFilterRequest;
import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.UserDtoResponse;
import com.rookie.asset_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping
  public ResponseEntity<ApiDtoResponse<PagingDtoResponse<UserDtoResponse>>> getAllUsers(
      @ModelAttribute UserFilterRequest userFilterRequest,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(defaultValue = "id") String sortBy,
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
}
