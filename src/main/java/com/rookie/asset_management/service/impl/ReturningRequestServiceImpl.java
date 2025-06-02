package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.return_request.CompleteReturningRequestDtoResponse;
import com.rookie.asset_management.dto.response.return_request.ReturningRequestDtoResponse;
import com.rookie.asset_management.entity.ReturningRequest;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.enums.AssignmentStatus;
import com.rookie.asset_management.enums.ReturningRequestStatus;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.mapper.PagingMapper;
import com.rookie.asset_management.repository.ReturningRequestRepository;
import com.rookie.asset_management.repository.SpecificationRepository;
import com.rookie.asset_management.repository.UserRepository;
import com.rookie.asset_management.service.JwtService;
import com.rookie.asset_management.service.ReturningRequestService;
import com.rookie.asset_management.service.specification.ReturningRequestSpecification;
import com.rookie.asset_management.util.SpecificationBuilder;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReturningRequestServiceImpl
    extends PagingServiceImpl<ReturningRequestDtoResponse, ReturningRequest, Integer>
    implements ReturningRequestService {

  ReturningRequestRepository returningRequestRepository;
  UserRepository userRepository;
  JwtService jwtService;

  public ReturningRequestServiceImpl(
      PagingMapper<ReturningRequest, ReturningRequestDtoResponse> pagingMapper,
      SpecificationRepository<ReturningRequest, Integer> specificationRepository,
      ReturningRequestRepository returningRequestRepository,
      UserRepository userRepository,
      JwtService jwtService) {
    super(pagingMapper, specificationRepository);
    this.returningRequestRepository = returningRequestRepository;
    this.jwtService = jwtService;
    this.userRepository = userRepository;
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

    // Update acceptedBy to the current user
    returningRequest.setAcceptedBy(user);

    // Save the updated request
    returningRequestRepository.save(returningRequest);

    return CompleteReturningRequestDtoResponse.builder()
        .id(returningRequest.getId())
        .status(returningRequest.getStatus().name())
        .build();
  }
}
