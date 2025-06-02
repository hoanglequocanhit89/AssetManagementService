package com.rookie.asset_management.constant;

public class Endpoints {

  //    Public endpoints go here
  public static final String[] PUBLIC_ENDPOINTS = {
    ApiPaths.V1 + "/auth/login", "/swagger-ui/**", "/v3/api-docs/**", "/actuator/**",
  };

  //    Admin endpoints go here
  public static final String[] ADMIN_ENDPOINTS = {
    ApiPaths.V1 + "/assets/**", "/assignments/**", "/return/**", ApiPaths.V1 + "/reports/**",
  };

  //   Staff endpoints go here
  public static final String[] STAFF_ENDPOINTS = {"abc"};
}
