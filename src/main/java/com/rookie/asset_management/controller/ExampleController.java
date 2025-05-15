package com.rookie.asset_management.controller;

import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.service.ExampleService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ExampleController is a REST controller that handles requests related to example operations.<br>
 * The controller must be annotated with {@link RestController} and {@link RequestMapping} to define
 * the base URL for the API.
 */
@RestController
@RequestMapping("/api/v1/example")
@RequiredArgsConstructor
public class ExampleController {
  // This is a placeholder for the actual implementation of the controller.
  // You can add methods to handle specific requests related to example operations.
  // For example, you can add methods to handle GET, POST, PUT, DELETE requests.
  // The return type of the methods must follow the API response structure defined in
  // ApiDtoResponse.

  // ExampleService is a placeholder for the actual service that will handle business logic.
  private final ExampleService exampleService;

  // Example method to handle a GET request
  @GetMapping
  public ResponseEntity<ApiDtoResponse<Map<String, String>>> getExample() {
    // Create a sample response
    String message = exampleService.exampleMethod();
    // Create a map to hold the test response data
    Map<String, String> data = Map.of("message", message);
    ApiDtoResponse<Map<String, String>> response =
        ApiDtoResponse.<Map<String, String>>builder().message("Success").data(data).build();
    return ResponseEntity.ok(response);
  }

  // other methods can be added here to handle different requests

}
