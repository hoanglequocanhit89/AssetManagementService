package com.rookie.asset_management.controller;

import com.rookie.asset_management.constant.ApiPaths;
import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.return_request.ReturningRequestDtoResponse;
import com.rookie.asset_management.enums.ReturningRequestStatus;
import com.rookie.asset_management.service.ReturningRequestService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
