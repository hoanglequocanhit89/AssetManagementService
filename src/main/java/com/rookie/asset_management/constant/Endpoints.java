package com.rookie.asset_management.constant;

public class Endpoints {

  //    Public endpoints go here
  public static final String[] PUBLIC_ENDPOINTS = {
    ApiPaths.V1 + "/auth/login",
    "/swagger-ui/**",
    "/v3/api-docs",
    "/v3/api-docs/**",
    "/v3/api-docs.yaml",
    "/actuator/**"
  };

  //    Admin endpoints go here
  public static final String[] ADMIN_ENDPOINTS = {ApiPaths.V1 + "/assets/**"};

  //   Staff endpoints go here
  public static final String[] STAFF_ENDPOINTS = {"abc"};
}
