package com.rookie.asset_management.controller;

import com.rookie.asset_management.constant.ApiPaths;
import com.rookie.asset_management.dto.request.assignment.CreateUpdateAssignmentRequest;
import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.assignment.AssignmentDetailDtoResponse;
import com.rookie.asset_management.dto.response.assignment.AssignmentDetailForEditResponse;
import com.rookie.asset_management.dto.response.assignment.AssignmentListDtoResponse;
import com.rookie.asset_management.dto.response.assignment.AssignmentStatusResponse;
import com.rookie.asset_management.dto.response.assignment.MyAssignmentDtoResponse;
import com.rookie.asset_management.enums.AssignmentStatus;
import com.rookie.asset_management.service.AssignmentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(ApiPaths.V1 + "/assignments")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentController {

  AssignmentService assignmentService;

  public AssignmentController(AssignmentService assignmentService) {
    this.assignmentService = assignmentService;
  }

  @GetMapping
  public ResponseEntity<ApiDtoResponse<PagingDtoResponse<AssignmentListDtoResponse>>>
      getAllAssignments(
          @RequestParam(required = false) AssignmentStatus status,
          @RequestParam(required = false) String assignedDate,
          @RequestParam(required = false) String query,
          @RequestParam(defaultValue = "0") Integer page,
          @RequestParam(defaultValue = "20") Integer size,
          @RequestParam(defaultValue = "assetCode") String sortBy,
          @RequestParam(defaultValue = "asc") String sortDir) {

    // Call service to get assignments
    PagingDtoResponse<AssignmentListDtoResponse> result =
        assignmentService.getAllAssignments(
            status, assignedDate, query, page, size, sortBy, sortDir);

    // Wrap result in ApiDtoResponse
    ApiDtoResponse<PagingDtoResponse<AssignmentListDtoResponse>> response =
        ApiDtoResponse.<PagingDtoResponse<AssignmentListDtoResponse>>builder()
            .message("Assignments retrieved successfully")
            .data(result)
            .build();

    return ResponseEntity.ok(response);
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

  @PutMapping("/{assignmentId}")
  public ResponseEntity<ApiDtoResponse<AssignmentListDtoResponse>> editAssignment(
      @PathVariable int assignmentId, @RequestBody @Valid CreateUpdateAssignmentRequest request) {
    return ResponseEntity.ok(
        ApiDtoResponse.<AssignmentListDtoResponse>builder()
            .message("Assignment updated successfully.")
            .data(assignmentService.editAssignment(assignmentId, request))
            .build());
  }

  @GetMapping("/{assignmentId}")
  public ResponseEntity<ApiDtoResponse<AssignmentDetailDtoResponse>> getAssignmentDetails(
      @PathVariable Integer assignmentId) {
    ApiDtoResponse<AssignmentDetailDtoResponse> response =
        assignmentService.getAssignmentDetails(assignmentId);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{assignmentId}")
  public ResponseEntity<ApiDtoResponse<Void>> deleteAssignment(@PathVariable Integer assignmentId) {
    ApiDtoResponse<Void> response = assignmentService.deleteAssignment(assignmentId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/me")
  public ResponseEntity<ApiDtoResponse<List<MyAssignmentDtoResponse>>> getMyAssignments(
      @RequestParam(defaultValue = "assetCode") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {
    ApiDtoResponse<List<MyAssignmentDtoResponse>> response =
        assignmentService.getMyAssignments(sortBy, sortDir);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{assignmentId}")
  public ResponseEntity<ApiDtoResponse<AssignmentStatusResponse>> respondToAssignment(
      @PathVariable int assignmentId, @RequestParam AssignmentStatus status) {
    return ResponseEntity.ok(
        ApiDtoResponse.<AssignmentStatusResponse>builder()
            .message("Assignment updated successfully.")
            .data(assignmentService.responseToAssignment(assignmentId, status))
            .build());
  }

  @GetMapping("/{assignmentId}/update")
  public ResponseEntity<ApiDtoResponse<AssignmentDetailForEditResponse>> getAssignmentDetailForEdit(
      @PathVariable Integer assignmentId) {
    return ResponseEntity.ok(
        ApiDtoResponse.<AssignmentDetailForEditResponse>builder()
            .message("Assignment retrieved successfully")
            .data(assignmentService.getAssignmentDetailForEdit(assignmentId))
            .build());
  }
}
