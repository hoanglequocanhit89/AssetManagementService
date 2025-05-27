package com.rookie.asset_management.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.crypto.SecretKey;

public interface JwtService {

  void generateToken(String username, HttpServletResponse response);

  String getJwtFromCookie(HttpServletRequest request);

  void validateToken(String token);

  void removeTokenFromCookie(HttpServletResponse response);

  SecretKey getSignInKey();

  String extractUsername();
}
