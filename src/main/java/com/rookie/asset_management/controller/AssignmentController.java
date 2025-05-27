package com.rookie.asset_management.controller;

import com.rookie.asset_management.dto.request.assignment.CreateUpdateAssignmentRequest;
import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.assignment.AssignmentListDtoResponse;
import com.rookie.asset_management.service.AssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/assignments")
public class AssignmentController extends ApiV1Controller {

  AssignmentService assignmentService;

  public AssignmentController(AssignmentService assignmentService) {
    this.assignmentService = assignmentService;
  }

  @PostMapping
  public ResponseEntity<ApiDtoResponse<AssignmentListDtoResponse>> createAssignment(
      @RequestBody @Valid CreateUpdateAssignmentRequest request) {
    // Return HTTP 201 Created with the assignment details in response body
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            ApiDtoResponse.<AssignmentListDtoResponse>builder()
                .message("Assignment created successfully.")
                .data(assignmentService.createAssignment(request))
                .build());
  }
}
