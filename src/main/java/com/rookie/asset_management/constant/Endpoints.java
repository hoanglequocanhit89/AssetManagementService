package com.rookie.asset_management.constant;

public class Endpoints {

  //    Public endpoints go here
  public static final String[] PUBLIC_ENDPOINTS = {
    ApiPaths.V1 + "/auth/login",
    ApiPaths.V1 + "/swagger-ui/**",
    ApiPaths.V1 + "/v3/api-docs/**",
    ApiPaths.V1 + "/actuator/**",
  };

  //    Admin endpoints go here
  public static final String[] ADMIN_ENDPOINTS = {
    ApiPaths.V1 + "/assets/**",
    ApiPaths.V1 + "/assignments/**",
    ApiPaths.V1 + "/return/**",
    ApiPaths.V1 + "/return/{assignmentId}",
    ApiPaths.V1 + "/return/me/{assignmentId}",
    ApiPaths.V1 + "/return/{returningRequestId}",
    ApiPaths.V1 + "/reports/**",
  };

  //   Staff endpoints go here
  public static final String[] STAFF_ENDPOINTS = {
    ApiPaths.V1 + "/return/*", ApiPaths.V1 + "/assignments/me"
  };

  public static final String[] ASSIGNMENT_STAFF_ENDPOINTS = {ApiPaths.V1 + "/assignments/*"};

  public static final String[] RETURNING_REQUEST_STAFF_ENDPOINTS = {
    ApiPaths.V1 + "/return/me/{assignmentId}"
  };
}
