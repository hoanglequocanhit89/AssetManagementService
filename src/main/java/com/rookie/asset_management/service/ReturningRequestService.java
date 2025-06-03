package com.rookie.asset_management.service;

import com.rookie.asset_management.dto.request.returning.CreateReturningRequestDtoRequest;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.return_request.CompleteReturningRequestDtoResponse;
import com.rookie.asset_management.dto.response.return_request.ReturningRequestDtoResponse;
import com.rookie.asset_management.dto.response.returning.ReturningRequestDetailDtoResponse;
import com.rookie.asset_management.enums.ReturningRequestStatus;

/**
 * ReturningRequestService defines the business operations for managing returning requests. It
 * provides functionalities to create, view, complete, or cancel returning requests.
 */
public interface ReturningRequestService {

  /**
   * Retrieves a paginated list of returning requests based on the provided filters.
   *
   * @param status the status of the returning requests to filter by
   * @param returnedDate the date the items were returned, used as a filter
   * @param query a search query to filter the returning requests
   * @param page the page number for pagination
   * @param size the number of items per page
   * @param sortBy the field to sort the results by
   * @param sortDir the direction of sorting (e.g., ASC or DESC)
   * @return a paginated response containing the filtered returning requests
   */
  PagingDtoResponse<ReturningRequestDtoResponse> getAllReturningRequests(
      ReturningRequestStatus status,
      String returnedDate,
      String query,
      Integer page,
      Integer size,
      String sortBy,
      String sortDir);

  /**
   * Completes a returning request by its unique identifier.
   *
   * @param id the unique identifier of the returning request to complete
   * @return a response containing the details of the completed returning request
   */
  CompleteReturningRequestDtoResponse completeReturningRequest(Integer id);

  /**
   * Creates a new returning request based on the provided request (for admin).
   *
   * @param request the DTO containing the details of the returning request to be created
   * @return the response DTO containing the details of the created returning request
   */
  ReturningRequestDetailDtoResponse createReturningRequest(
      CreateReturningRequestDtoRequest request);

  /**
   * Creates a new returning request for the current user.
   *
   * @param request the DTO containing the details of the returning request to be created
   * @return the response DTO containing the details of the created returning request
   */
  ReturningRequestDetailDtoResponse createUserReturningRequest(
      CreateReturningRequestDtoRequest request);

  /**
   * Cancels a returning request by its ID (for admin).
   *
   * @param returningRequestId the ID of the returning request to cancel
   * @return the response DTO containing the details of the cancelled returning request
   */
  ReturningRequestDetailDtoResponse cancelReturningRequest(Integer returningRequestId);
}
