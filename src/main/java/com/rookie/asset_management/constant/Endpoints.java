package com.rookie.asset_management.constant;

public class Endpoints {

  //    Public endpoints go here
  public static final String[] PUBLIC_ENDPOINTS = {
    ApiPaths.V1 + "/auth/login",
    "/swagger-ui/**",
    "/swagger-ui**",
    "/v3/api-docs/**",
    "/v3/api-docs**",
    "/actuator/**",
  };

  //    Admin endpoints go here
  public static final String[] ADMIN_ENDPOINTS = {
    ApiPaths.V1 + "/assets/**",
    ApiPaths.V1 + "/assignments/**",
    ApiPaths.V1 + "/return/**",
    ApiPaths.V1 + "/reports/**",
    ApiPaths.V1 + "/exports/**",
  };

  //   Staff endpoints go here
  public static final String[] STAFF_ENDPOINTS = {
    ApiPaths.V1 + "/return/*", ApiPaths.V1 + "/assignments/*"
  };

  public static final String[] ASSIGNMENT_STAFF_PATCH_ENDPOINTS = {ApiPaths.V1 + "/assignments/*"};

  public static final String[] RETURNING_REQUEST_STAFF_ENDPOINTS = {
    ApiPaths.V1 + "/return/me/{assignmentId}"
  };
}
