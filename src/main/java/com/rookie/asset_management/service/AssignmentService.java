package com.rookie.asset_management.service;

import com.rookie.asset_management.dto.request.assignment.CreateUpdateAssignmentRequest;
import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.assignment.AssignmentDetailDtoResponse;
import com.rookie.asset_management.dto.response.assignment.AssignmentListDtoResponse;
import com.rookie.asset_management.dto.response.assignment.MyAssignmentDtoResponse;
import com.rookie.asset_management.enums.AssignmentStatus;
import java.util.List;

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


  /**
   * Retrieves a paginated list of assignments filtered by status, assigned date, and a search query.
   *
   * @param status      the status of the assignment to filter by (optional)
   * @param assignedDate the assigned date to filter by in a specific format (optional)
   * @param query       a search keyword to filter assignments by title or description (optional)
   * @param page        the page number for pagination (zero-based index)
   * @param size        the number of items per page
   * @param sortBy      the field name to sort by
   * @param sortDir     the direction of sorting ("asc" or "desc")
   * @return a {@link PagingDtoResponse} containing a list of {@link AssignmentListDtoResponse} objects
   */
  PagingDtoResponse<AssignmentListDtoResponse> getAllAssignments(
      AssignmentStatus status,
      String assignedDate,
      String query,
      Integer page,
      Integer size,
      String sortBy,
      String sortDir);

  /**
   * Retrieves detailed information of a specific assignment by its ID.
   *
   * @param assignmentId the ID of the assignment to retrieve
   * @return an {@link ApiDtoResponse} containing the {@link AssignmentDetailDtoResponse}
   */
  ApiDtoResponse<AssignmentDetailDtoResponse> getAssignmentDetails(Integer assignmentId);

  /**
   * Deletes a specific assignment by its ID.
   *
   * @param assignmentId the ID of the assignment to delete
   * @return an {@link ApiDtoResponse} with no content if the deletion was successful
   */
  ApiDtoResponse<Void> deleteAssignment(Integer assignmentId);

  /**
   * Retrieves a list of assignments assigned to the current user.
   *
   * @param sortBy  the field name to sort the results by
   * @param sortDir the direction of sorting ("asc" or "desc")
   * @return an {@link ApiDtoResponse} containing a list of {@link MyAssignmentDtoResponse}
   */
  ApiDtoResponse<List<MyAssignmentDtoResponse>> getMyAssignments(String sortBy, String sortDir);
}
