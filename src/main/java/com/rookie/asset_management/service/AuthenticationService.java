package com.rookie.asset_management.service;

import com.rookie.asset_management.dto.request.authentication.ChangePasswordRequestDTO;
import com.rookie.asset_management.dto.request.authentication.LoginRequestDTO;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {

  String login(LoginRequestDTO loginRequestDTO, HttpServletResponse response);

  String changePassword(
      ChangePasswordRequestDTO changePasswordRequestDTO, HttpServletResponse response);

  void logout(HttpServletResponse response);
}
