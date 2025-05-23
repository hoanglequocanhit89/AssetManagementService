package com.rookie.asset_management.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

@CrossOrigin(
    origins = {"http://localhost:3000", "https://proud-smoke-06841df00.6.azurestaticapps.net"},
    allowCredentials = "true",
    allowedHeaders = "*",
    methods = {
      RequestMethod.GET,
      RequestMethod.POST,
      RequestMethod.PUT,
      RequestMethod.DELETE,
      RequestMethod.OPTIONS,
      RequestMethod.HEAD,
      RequestMethod.PATCH
    })
public abstract class ApiV1Controller {}
