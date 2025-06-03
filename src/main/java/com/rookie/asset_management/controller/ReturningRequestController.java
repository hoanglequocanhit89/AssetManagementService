package com.rookie.asset_management.controller;

import com.rookie.asset_management.constant.ApiPaths;
import com.rookie.asset_management.dto.request.returning.CreateReturningRequestDtoRequest;
import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.return_request.CompleteReturningRequestDtoResponse;
import com.rookie.asset_management.dto.response.return_request.ReturningRequestDtoResponse;
import com.rookie.asset_management.dto.response.returning.ReturningRequestDetailDtoResponse;
import com.rookie.asset_management.enums.ReturningRequestStatus;
import com.rookie.asset_management.service.ReturningRequestService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Validated
@RestController
@RequestMapping(ApiPaths.V1 + "/return")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReturningRequestController {

  ReturningRequestService returningRequestService;

  @GetMapping
  public ResponseEntity<ApiDtoResponse<PagingDtoResponse<ReturningRequestDtoResponse>>>
      getAllReturningRequests(
          @RequestParam(required = false) ReturningRequestStatus status,
          @RequestParam(required = false) String returnedDate,
          @RequestParam(required = false) String query,
          @RequestParam(defaultValue = "0") Integer page,
          @RequestParam(defaultValue = "20") Integer size,
          @RequestParam(defaultValue = "assetCode") String sortBy,
          @RequestParam(defaultValue = "asc") String sortDir) {

    // Call service to get returning requests
    PagingDtoResponse<ReturningRequestDtoResponse> result =
        returningRequestService.getAllReturningRequests(
            status, returnedDate, query, page, size, sortBy, sortDir);

    // Wrap result in ApiDtoResponse
    ApiDtoResponse<PagingDtoResponse<ReturningRequestDtoResponse>> response =
        ApiDtoResponse.<PagingDtoResponse<ReturningRequestDtoResponse>>builder()
            .message("Returning requests retrieved successfully")
            .data(result)
            .build();

    return ResponseEntity.ok(response);
  }

  @PatchMapping("{id}/complete")
  public ResponseEntity<ApiDtoResponse<Void>> completeReturningRequest(@PathVariable Integer id) {

    // Call service to complete returning request
    CompleteReturningRequestDtoResponse result =
        returningRequestService.completeReturningRequest(id);

    // Create success response
    ApiDtoResponse<Void> response =
        ApiDtoResponse.<Void>builder()
            .message(
                result != null
                    ? "Returning request completed successfully"
                    : "Request has been completed already")
            .build();

    return ResponseEntity.ok(response);
  }

  // Endpoint for Admin create request for returning asset
  @PostMapping
  public ResponseEntity<ApiDtoResponse<ReturningRequestDetailDtoResponse>> createReturningRequest(
      @RequestBody @Valid CreateReturningRequestDtoRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            ApiDtoResponse.<ReturningRequestDetailDtoResponse>builder()
                .message("Returning request created successfully.")
                .data(returningRequestService.createReturningRequest(request))
                .build());
  }

  // Endpoint for user create request for returning asset
  @PostMapping("/me")
  public ResponseEntity<ApiDtoResponse<ReturningRequestDetailDtoResponse>>
      createUserReturningRequest(@RequestBody @Valid CreateReturningRequestDtoRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            ApiDtoResponse.<ReturningRequestDetailDtoResponse>builder()
                .message("Returning request created successfully by user.")
                .data(returningRequestService.createUserReturningRequest(request))
                .build());
  }

  // Endpoint for Admin cancel request for returning asset
  @DeleteMapping("/{returningRequestId}")
  public ResponseEntity<ApiDtoResponse<ReturningRequestDetailDtoResponse>> cancelReturningRequest(
      @PathVariable Integer returningRequestId) {
    return ResponseEntity.ok(
        ApiDtoResponse.<ReturningRequestDetailDtoResponse>builder()
            .message("Returning request cancelled successfully.")
            .data(returningRequestService.cancelReturningRequest(returningRequestId))
            .build());
  }
}
