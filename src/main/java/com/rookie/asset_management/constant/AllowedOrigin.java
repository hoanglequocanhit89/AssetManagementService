package com.rookie.asset_management.constant;

public final class AllowedOrigin {
  public static final String[] ALLOWED_ORIGINS = {
    "http://localhost:3000", "https://proud-smoke-06841df00.6.azurestaticapps.net",
  };

  public static final String[] ALLOWED_METHODS = {
    "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
  };

  public static final String[] ALLOWED_HEADERS = {
    "Content-Type", "Authorization", "X-Requested-With", "Accept", "Origin", "Cache-Control"
  };
}
