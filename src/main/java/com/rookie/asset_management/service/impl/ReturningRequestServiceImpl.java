package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.return_request.CompleteReturningRequestDtoResponse;
import com.rookie.asset_management.dto.response.return_request.ReturningRequestDtoResponse;
import com.rookie.asset_management.dto.response.returning.ReturningRequestDetailDtoResponse;
import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.ReturningRequest;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.enums.AssetStatus;
import com.rookie.asset_management.enums.AssignmentStatus;
import com.rookie.asset_management.enums.ReturningRequestStatus;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.mapper.ReturningRequestMapper;
import com.rookie.asset_management.repository.AssignmentRepository;
import com.rookie.asset_management.repository.ReturningRequestRepository;
import com.rookie.asset_management.repository.UserRepository;
import com.rookie.asset_management.service.JwtService;
import com.rookie.asset_management.service.ReturningRequestService;
import com.rookie.asset_management.service.abstraction.PagingServiceImpl;
import com.rookie.asset_management.service.specification.ReturningRequestSpecification;
import com.rookie.asset_management.util.SpecificationBuilder;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReturningRequestServiceImpl
    extends PagingServiceImpl<ReturningRequestDtoResponse, ReturningRequest, Integer>
    implements ReturningRequestService {

  ReturningRequestRepository returningRequestRepository;
  AssignmentRepository assignmentRepository;
  UserRepository userRepository;
  ReturningRequestMapper returningRequestMapper;
  JwtService jwtService;

  @Autowired
  public ReturningRequestServiceImpl(
      ReturningRequestRepository returningRequestRepository,
      AssignmentRepository assignmentRepository,
      UserRepository userRepository,
      ReturningRequestMapper returningRequestMapper,
      JwtService jwtService) {
    super(returningRequestMapper, returningRequestRepository);
    this.returningRequestRepository = returningRequestRepository;
    this.userRepository = userRepository;
    this.returningRequestMapper = returningRequestMapper;
    this.jwtService = jwtService;
    this.assignmentRepository = assignmentRepository;
  }

  @Override
  public PagingDtoResponse<ReturningRequestDtoResponse> getAllReturningRequests(
      ReturningRequestStatus status,
      String returnedDate,
      String query,
      Integer page,
      Integer size,
      String sortBy,
      String sortDir) {

    String username = jwtService.extractUsername();
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "User Not Found"));

    // Check if user is admin
    if (!"ADMIN".equalsIgnoreCase(user.getRole().getName())) {
      throw new AppException(HttpStatus.FORBIDDEN, "Only admins can access this endpoint");
    }

    // Adjust sortBy for nested properties
    String effectiveSortBy = sortBy;

    if ("assetName".equals(sortBy)) {
      effectiveSortBy = "assignment.asset.name";
    } else if ("assetCode".equals(sortBy)) {
      effectiveSortBy = "assignment.asset.assetCode";
    } else if ("requestedBy".equals(sortBy)) {
      effectiveSortBy = "requestedBy.username";
    } else if ("acceptedBy".equals(sortBy)) {
      effectiveSortBy = "acceptedBy.username";
    } else if ("assignedDate".equals(sortBy)) {
      effectiveSortBy = "assignment.assignedDate";
    }

    Pageable pageable;
    if ("status".equals(sortBy)) {
      pageable = Pageable.unpaged();
    } else {
      pageable = createPageable(page, size, sortDir, effectiveSortBy);
    }

    // Build specification
    Specification<ReturningRequest> spec =
        new SpecificationBuilder<ReturningRequest>()
            .addIfNotNull(status, ReturningRequestSpecification.hasStatus(status))
            .addIfNotNull(returnedDate, ReturningRequestSpecification.hasReturnedDate(returnedDate))
            .addIfNotNull(query, ReturningRequestSpecification.hasAssetOrRequesterLike(query))
            .addIfNotNull(
                user.getId(), ReturningRequestSpecification.excludeAdminRequests(user.getId()))
            .addIfNotNull(
                user.getId(), ReturningRequestSpecification.hasSameLocationAs(user.getId()))
            .build();

    // Apply alphabetical sorting for status
    if ("status".equals(sortBy)) {
      String customerSortDir = sortDir.equals("asc") ? "desc" : "asc";
      spec =
          Specification.where(spec)
              .and(ReturningRequestSpecification.orderByStatus(customerSortDir));
    }

    // Use getMany from PagingServiceImpl
    PagingDtoResponse<ReturningRequestDtoResponse> result = getMany(spec, pageable);

    return result;
  }

  @Override
  public CompleteReturningRequestDtoResponse completeReturningRequest(Integer id) {
    ReturningRequest returningRequest =
        returningRequestRepository
            .findById(id)
            .orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, "Returning Request Not Found"));

    String username = jwtService.extractUsername();
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "User Not Found"));

    // Check if user is admin
    if (!"ADMIN".equalsIgnoreCase(user.getRole().getName())) {
      throw new AppException(HttpStatus.FORBIDDEN, "Only admins can access this endpoint");
    }

    // Check if admin has the same location as the returning request
    if (!returningRequest
        .getAssignment()
        .getAsset()
        .getLocation()
        .getId()
        .equals(user.getLocation().getId())) {
      throw new AppException(
          HttpStatus.FORBIDDEN, "You do not have permission to complete this request");
    }

    // Check if the request is already completed
    if (returningRequest.getStatus() == ReturningRequestStatus.COMPLETED) {
      return null;
    }

    // Update the status to COMPLETED
    returningRequest.setStatus(ReturningRequestStatus.COMPLETED);

    // Update the returned date to the current date
    returningRequest.setReturnedDate(LocalDate.now());

    // Update status of the assignment to RETURNED
    returningRequest.getAssignment().setStatus(AssignmentStatus.RETURNED);

    // Update status of asset to AVAILABLE
    returningRequest.getAssignment().getAsset().setStatus(AssetStatus.AVAILABLE);

    // Update acceptedBy to the current user
    returningRequest.setAcceptedBy(user);

    // Save the updated request
    returningRequestRepository.save(returningRequest);

    return CompleteReturningRequestDtoResponse.builder()
        .id(returningRequest.getId())
        .status(returningRequest.getStatus().name())
        .build();
  }

  @Override
  @Transactional
  public ReturningRequestDetailDtoResponse createReturningRequest(Integer assignmentId) {
    // Get current admin from JWT
    String username = jwtService.extractUsername();
    User admin =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "User Not Found"));

    if (!"ADMIN".equalsIgnoreCase(admin.getRole().getName())) {
      throw new AppException(HttpStatus.FORBIDDEN, "Only admins can create returning requests");
    }

    // Find the assignment
    Assignment assignment =
        assignmentRepository
            .findById(assignmentId)
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Assignment Not Found"));

    // Check if assignment is in ACCEPTED state
    if (!assignment.getStatus().equals(AssignmentStatus.ACCEPTED)) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, "Only accepted assignments can have returning requests");
    }

    // Check location consistency
    if (!assignment.getAsset().getLocation().getId().equals(admin.getLocation().getId())
        || !assignment.getAssignedTo().getLocation().getId().equals(admin.getLocation().getId())) {
      throw new AppException(HttpStatus.FORBIDDEN, "Assignment not in admin's location");
    }

    // Create new returning request
    ReturningRequest returningRequest = new ReturningRequest();
    returningRequest.setAssignment(assignment);
    returningRequest.setRequestedBy(admin);
    returningRequest.setReturnedDate(LocalDate.now());
    returningRequest.setStatus(ReturningRequestStatus.WAITING);

    // Update assignment status to WAITING_FOR_RETURNING
    assignment.setStatus(AssignmentStatus.WAITING_FOR_RETURNING);
    assignmentRepository.save(assignment);

    // Save and return
    return returningRequestMapper.toDetailDto(returningRequestRepository.save(returningRequest));
  }

  @Override
  @Transactional
  public ReturningRequestDetailDtoResponse createUserReturningRequest(Integer assignmentId) {
    // Get current user from JWT
    String username = jwtService.extractUsername();
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "User Not Found"));

    // Find the assignment
    Assignment assignment =
        assignmentRepository
            .findById(assignmentId)
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Assignment Not Found"));

    // Check if the user is the one assigned to this assignment
    if (!assignment.getAssignedTo().getId().equals(user.getId())) {
      throw new AppException(
          HttpStatus.FORBIDDEN, "You can only create returning requests for your own assignments");
    }

    // Check if assignment is in ACCEPTED state
    if (!assignment.getStatus().equals(AssignmentStatus.ACCEPTED)) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, "Only accepted assignments can have returning requests");
    }

    // Create new returning request
    ReturningRequest returningRequest = new ReturningRequest();
    returningRequest.setAssignment(assignment);
    returningRequest.setRequestedBy(user);
    returningRequest.setReturnedDate(LocalDate.now());
    returningRequest.setStatus(ReturningRequestStatus.WAITING);

    // Update assignment status to WAITING_FOR_RETURNING
    assignment.setStatus(AssignmentStatus.WAITING_FOR_RETURNING);
    assignmentRepository.save(assignment);

    // Save and return
    return returningRequestMapper.toDetailDto(returningRequestRepository.save(returningRequest));
  }

  @Override
  @Transactional
  public ReturningRequestDetailDtoResponse cancelReturningRequest(Integer returningRequestId) {
    // Get current admin from JWT
    String username = jwtService.extractUsername();
    User admin =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "User Not Found"));

    if (!"ADMIN".equalsIgnoreCase(admin.getRole().getName())) {
      throw new AppException(HttpStatus.FORBIDDEN, "Only admins can cancel returning requests");
    }

    // Find the returning request
    ReturningRequest returningRequest =
        returningRequestRepository
            .findById(returningRequestId)
            .orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, "Returning request not found"));

    // Check if the request is in WAITING state
    if (!returningRequest.getStatus().equals(ReturningRequestStatus.WAITING)) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, "Only waiting returning requests can be cancelled");
    }

    // Check location consistency
    Assignment assignment = returningRequest.getAssignment();
    if (!assignment.getAsset().getLocation().getId().equals(admin.getLocation().getId())
        || !assignment.getAssignedTo().getLocation().getId().equals(admin.getLocation().getId())) {
      throw new AppException(HttpStatus.FORBIDDEN, "Returning request not in admin's location");
    }

    // Update assignment status back to ACCEPTED
    assignment.setStatus(AssignmentStatus.ACCEPTED);
    assignment.setReturningRequest(null); // Clear the relationship
    assignmentRepository.save(assignment);

    // Hard delete the returning request
    returningRequestRepository.delete(returningRequest);

    // Return the deleted returning request details
    return returningRequestMapper.toDetailDto(returningRequest);
  }
}
