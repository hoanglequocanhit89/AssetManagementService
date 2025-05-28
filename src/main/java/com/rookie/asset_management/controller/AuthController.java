package com.rookie.asset_management.controller;

import com.rookie.asset_management.constant.ApiPaths;
import com.rookie.asset_management.dto.request.authentication.ChangePasswordRequestDTO;
import com.rookie.asset_management.dto.request.authentication.LoginRequestDTO;
import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.authentication.LoginResponseDTO;
import com.rookie.asset_management.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(ApiPaths.V1 + "/auth")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthController {

  AuthenticationService authenticationService;

  @PostMapping("/login")
  public ResponseEntity<ApiDtoResponse<LoginResponseDTO>> login(
      @RequestBody LoginRequestDTO request, HttpServletResponse response) {
    ApiDtoResponse<LoginResponseDTO> responseDTO =
        ApiDtoResponse.<LoginResponseDTO>builder()
            .message("Login successfully!")
            .data(authenticationService.login(request, response))
            .build();
    return ResponseEntity.ok(responseDTO);
  }

  @PatchMapping("/change-password")
  public ResponseEntity<ApiDtoResponse<Void>> changePassword(
      @RequestBody ChangePasswordRequestDTO request, HttpServletResponse response) {
    ApiDtoResponse<Void> responseDTO =
        ApiDtoResponse.<Void>builder()
            .message(authenticationService.changePassword(request, response))
            .build();
    return ResponseEntity.ok(responseDTO);
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiDtoResponse<Void>> logout(HttpServletResponse response) {
    authenticationService.logout(response);
    ApiDtoResponse<Void> responseDTO =
        ApiDtoResponse.<Void>builder().message("Logout successfully").build();
    return ResponseEntity.ok(responseDTO);
  }
}
