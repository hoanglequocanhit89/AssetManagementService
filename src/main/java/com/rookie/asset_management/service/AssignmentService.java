package com.rookie.asset_management.service;

import com.rookie.asset_management.dto.request.assignment.CreateUpdateAssignmentRequest;
import com.rookie.asset_management.dto.response.assignment.AssignmentListDtoResponse;

/**
 * AssignmentService defines the business operations related to assignment management. It provides
 * functionalities for creating and editing and retrieving assignments with filtering and sorting.
 */
public interface AssignmentService {

  /**
   * Creates a new assignment based on the provided request.
   *
   * @param request the DTO containing the details of the assignment to be created
   * @return the response DTO containing the details of the created assignment
   */
  AssignmentListDtoResponse createAssignment(CreateUpdateAssignmentRequest request);

  /**
   * Edits an existing assignment based on the provided assignment ID and request.
   *
   * @param assignmentId the ID of the assignment to be edited
   * @param request the DTO containing the updated details of the assignment
   * @return the response DTO containing the details of the updated assignment
   */
  AssignmentListDtoResponse editAssignment(int assignmentId, CreateUpdateAssignmentRequest request);
}
