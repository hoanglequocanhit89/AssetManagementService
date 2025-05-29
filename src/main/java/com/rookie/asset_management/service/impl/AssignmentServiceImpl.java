package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.dto.request.assignment.CreateUpdateAssignmentRequest;
import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.assignment.AssignmentDetailDtoResponse;
import com.rookie.asset_management.dto.response.assignment.AssignmentDetailForEditResponse;
import com.rookie.asset_management.dto.response.assignment.AssignmentListDtoResponse;
import com.rookie.asset_management.dto.response.assignment.AssignmentStatusResponse;
import com.rookie.asset_management.dto.response.assignment.MyAssignmentDtoResponse;
import com.rookie.asset_management.entity.Asset;
import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.enums.AssetStatus;
import com.rookie.asset_management.enums.AssignmentStatus;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.mapper.AssignmentMapper;
import com.rookie.asset_management.repository.AssetRepository;
import com.rookie.asset_management.repository.AssignmentRepository;
import com.rookie.asset_management.repository.UserRepository;
import com.rookie.asset_management.service.AssignmentService;
import com.rookie.asset_management.service.JwtService;
import com.rookie.asset_management.service.specification.AssignmentSpecification;
import com.rookie.asset_management.util.SpecificationBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentServiceImpl
    extends PagingServiceImpl<AssignmentListDtoResponse, Assignment, Integer>
    implements AssignmentService {
  AssignmentRepository assignmentRepository;
  AssignmentMapper assignmentMapper;
  UserRepository userRepository;
  AssetRepository assetRepository;
  JwtService jwtService;

  @Autowired
  public AssignmentServiceImpl(
      AssignmentRepository assignmentRepository,
      AssignmentMapper assignmentMapper,
      UserRepository userRepository,
      AssetRepository assetRepository,
      JwtService jwtService) {
    super(assignmentMapper, assignmentRepository);
    this.assignmentMapper = assignmentMapper;
    this.assignmentRepository = assignmentRepository;
    this.userRepository = userRepository;
    this.assetRepository = assetRepository;
    this.jwtService = jwtService;
  }

  @Override
  @Transactional
  public AssignmentListDtoResponse createAssignment(CreateUpdateAssignmentRequest request) {
    // check if the user and asset exist
    User assignee =
        userRepository
            .findById(request.getUserId())
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "User Not Found"));
    Asset asset =
        assetRepository
            .findById(request.getAssetId())
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Asset Not Found"));

    // check if the asset is available for assignment
    if (!asset.getStatus().equals(AssetStatus.AVAILABLE)) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Asset is not available for assignment");
    }

    // check if asset doesn't have any assignment in WAITING status
    if (assignmentRepository.existsByAssetAndStatusAndDeletedFalse(
        asset, AssignmentStatus.WAITING)) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Asset already has a waiting assignment");
    }

    // Get the user from JWT token
    String username = jwtService.extractUsername();
    User assigner =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "User Not Found"));

    // check if assigner and assignee are in the same location
    if (!assigner.getLocation().equals(assignee.getLocation())) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, "Assigner and assignee must be in the same location");
    }

    // check if the asset is in the same location with assigner
    if (!asset.getLocation().equals(assigner.getLocation())) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, "Asset must be in the same location with assigner");
    }

    // Create a new assignment
    Assignment assignment =
        Assignment.builder()
            .asset(asset)
            .assignedTo(assignee)
            .assignedDate(request.getAssignedDate())
            .note(request.getNote())
            .assignedBy(assigner)
            .status(AssignmentStatus.WAITING)
            .build();

    // Save the assignment
    return assignmentMapper.toDto(assignmentRepository.save(assignment));
  }

  @Override
  @Transactional
  public AssignmentListDtoResponse editAssignment(
      int assignmentId, CreateUpdateAssignmentRequest request) {
    // Find the existing assignment
    Assignment assignment =
        assignmentRepository
            .findById(assignmentId)
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Assignment Not Found"));

    // Check if assignment is in WAITING state
    if (!assignment.getStatus().equals(AssignmentStatus.WAITING)) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, "Only assignments in WAITING state can be edited");
    }

    // check if the user and asset exist
    User assignee =
        userRepository
            .findById(request.getUserId())
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "User Not Found"));

    Asset asset =
        assetRepository
            .findById(request.getAssetId())
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Asset Not Found"));

    // check if assigner and assignee are in the same location
    if (!assignment.getAssignedBy().getLocation().equals(assignee.getLocation())) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, "Assigner and assignee must be in the same location");
    }

    // check if the asset is available for assignment
    if (!asset.getStatus().equals(AssetStatus.AVAILABLE)) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Asset is not available for assignment");
    }

    // check if asset doesn't have any assignment in WAITING status
    if ((!Objects.equals(assignment.getAsset().getId(), request.getAssetId())
        && assignmentRepository.existsByAssetAndStatusAndDeletedFalse(
            asset, AssignmentStatus.WAITING))) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Asset already has a waiting assignment");
    }

    // check if the asset is in the same location with assigner
    if (!asset.getLocation().equals(assignment.getAssignedBy().getLocation())) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, "Asset must be in the same location with assigner");
    }

    // Update the assignment details
    assignment.setAssignedTo(assignee);
    assignment.setAsset(asset);
    assignment.setNote(request.getNote());
    assignment.setAssignedDate(request.getAssignedDate());

    // Save the updated assignment
    return assignmentMapper.toDto(assignmentRepository.save(assignment));
  }

  @Override
  public PagingDtoResponse<AssignmentListDtoResponse> getAllAssignments(
      AssignmentStatus status,
      String assignedDate,
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
      effectiveSortBy = "asset.name";
    } else if ("assetCode".equals(sortBy)) {
      effectiveSortBy = "asset.assetCode";
    } else if ("assignedTo".equals(sortBy)) {
      effectiveSortBy = "assignedTo.userProfile.lastName";
    } else if ("assignedBy".equals(sortBy)) {
      effectiveSortBy = "assignedBy.userProfile.lastName";
    }

    Pageable pageable;
    if ("status".equals(sortBy)) {
      pageable = Pageable.unpaged();
    } else {
      pageable = createPageable(page, size, sortDir, effectiveSortBy);
    }

    // Build specification
    Specification<Assignment> spec =
        new SpecificationBuilder<Assignment>()
            .addIfNotNull(status, AssignmentSpecification.hasStatus(status))
            .addIfNotNull(assignedDate, AssignmentSpecification.hasAssignedDate(assignedDate))
            .addIfNotNull(query, AssignmentSpecification.hasAssetOrAssigneeLike(query))
            .addIfNotNull(user.getId(), AssignmentSpecification.hasSameLocationAs(user.getId()))
            .add(AssignmentSpecification.excludeDeleted())
            .build();

    // Apply alphabetical sorting for status
    if ("status".equals(sortBy)) {
      String customerSortDir = sortDir.equals("asc") ? "desc" : "asc";
      spec = Specification.where(spec).and(AssignmentSpecification.orderByStatus(customerSortDir));
    }

    // Use getMany from PagingServiceImpl
    PagingDtoResponse<AssignmentListDtoResponse> result = getMany(spec, pageable);

    return result;
  }

  @Override
  public ApiDtoResponse<AssignmentDetailDtoResponse> getAssignmentDetails(Integer assignmentId) {
    String username = jwtService.extractUsername();
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "User Not Found"));

    if (!"ADMIN".equalsIgnoreCase(user.getRole().getName())) {
      throw new AppException(HttpStatus.FORBIDDEN, "Only admins can access this endpoint");
    }

    Assignment assignment =
        assignmentRepository
            .findByIdAndDeletedFalse(assignmentId)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Assignment not found"));

    if (!assignment.getAsset().getLocation().getId().equals(user.getLocation().getId())
        || !assignment.getAssignedTo().getLocation().getId().equals(user.getLocation().getId())) {
      throw new AppException(HttpStatus.FORBIDDEN, "Assignment not in admin's location");
    }

    AssignmentDetailDtoResponse response = assignmentMapper.toDetailDto(assignment);
    return ApiDtoResponse.<AssignmentDetailDtoResponse>builder()
        .message("Assignment details retrieved successfully")
        .data(response)
        .build();
  }

  @Override
  public ApiDtoResponse<Void> deleteAssignment(Integer assignmentId) {
    String username = jwtService.extractUsername();
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "User Not Found"));

    if (!"ADMIN".equalsIgnoreCase(user.getRole().getName())) {
      throw new AppException(HttpStatus.FORBIDDEN, "Only admins can access this endpoint");
    }

    Assignment assignment =
        assignmentRepository
            .findByIdAndDeletedFalse(assignmentId)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Assignment not found"));

    if (!assignment.getAsset().getLocation().getId().equals(user.getLocation().getId())
        || !assignment.getAssignedTo().getLocation().getId().equals(user.getLocation().getId())) {
      throw new AppException(HttpStatus.FORBIDDEN, "Assignment not in admin's location");
    }

    if (assignment.getStatus() != AssignmentStatus.WAITING
        && assignment.getStatus() != AssignmentStatus.DECLINED) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, "Can only delete assignments with status WAITING or DECLINED");
    }

    assignment.setDeleted(true);
    assignmentRepository.save(assignment);

    return ApiDtoResponse.<Void>builder()
        .message("Assignment deleted successfully")
        .data(null)
        .build();
  }

  @Override
  public ApiDtoResponse<List<MyAssignmentDtoResponse>> getMyAssignments(
      String sortBy, String sortDir) {

    String username = jwtService.extractUsername();
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "User Not Found"));

    // Build specification
    Specification<Assignment> spec =
        new SpecificationBuilder<Assignment>()
            .add(AssignmentSpecification.hasAssignedTo(user.getId()))
            .add(
                AssignmentSpecification.hasStatusIn(
                    Arrays.asList(AssignmentStatus.WAITING, AssignmentStatus.ACCEPTED)))
            .add(AssignmentSpecification.excludeDeleted())
            .build();

    List<Assignment> assignments;

    if ("status".equalsIgnoreCase(sortBy)) {
      assignments = assignmentRepository.findAll(spec);

      List<AssignmentStatus> statusOrder =
          sortDir.equalsIgnoreCase("asc")
              ? List.of(AssignmentStatus.ACCEPTED, AssignmentStatus.WAITING)
              : List.of(AssignmentStatus.WAITING, AssignmentStatus.ACCEPTED);

      assignments.sort(
          (a1, a2) -> {
            int i1 = statusOrder.indexOf(a1.getStatus());
            int i2 = statusOrder.indexOf(a2.getStatus());
            return Integer.compare(i1, i2);
          });

    } else {
      // Adjust sortBy for nested properties
      String effectiveSortBy;
      switch (sortBy) {
        case "assetName":
          effectiveSortBy = "asset.name";
          break;
        case "assetCode":
          effectiveSortBy = "asset.assetCode";
          break;
        case "category":
          effectiveSortBy = "asset.category.name";
          break;
        case "assignedDate":
          effectiveSortBy = "assignedDate";
          break;
        case "id":
          effectiveSortBy = "id";
          break;
        default:
          effectiveSortBy = "asset.assetCode";
      }

      Sort sort =
          sortDir.equalsIgnoreCase("asc")
              ? Sort.by(effectiveSortBy).ascending()
              : Sort.by(effectiveSortBy).descending();

      assignments = assignmentRepository.findAll(spec, sort);
    }

    List<MyAssignmentDtoResponse> response =
        assignments.stream().map(assignmentMapper::toMyAssignmentDto).collect(Collectors.toList());

    return ApiDtoResponse.<List<MyAssignmentDtoResponse>>builder()
        .message("My assignments retrieved successfully")
        .data(response)
        .build();
  }

  @Override
  @Transactional
  public AssignmentStatusResponse responseToAssignment(int assignmentId, AssignmentStatus status) {
    // Check if the updated status is valid
    if (!status.equals(AssignmentStatus.ACCEPTED) && !status.equals(AssignmentStatus.DECLINED)) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Status must be either ACCEPTED or DECLINED");
    }

    // Find the existing assignment
    Assignment assignment =
        assignmentRepository
            .findById(assignmentId)
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Assignment Not Found"));

    // Check if the assignment is in WAITING state
    if (!assignment.getStatus().equals(AssignmentStatus.WAITING)) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, "Only assignments in WAITING state can be responded to");
    }

    // Get username from token
    String username = jwtService.extractUsername();

    // Check if the user responding to the assignment is the one assigned to it
    if (!assignment.getAssignedTo().getUsername().equals(username)) {
      throw new AppException(
          HttpStatus.FORBIDDEN, "You are not authorized to respond to this assignment");
    }

    if (status.equals(AssignmentStatus.DECLINED)) {
      // If the status is DECLINED, set the assignment status to DECLINED
      assignment.setStatus(AssignmentStatus.DECLINED);
    } else {
      // If the status is ACCEPTED, set the assignment status to ACCEPTED
      assignment.setStatus(AssignmentStatus.ACCEPTED);
      // Update the asset status to ASSIGNED
      Asset asset = assignment.getAsset();
      asset.setStatus(AssetStatus.ASSIGNED);
      assetRepository.save(asset);
    }

    // Save the updated assignment
    assignmentRepository.save(assignment);

    return AssignmentStatusResponse.builder()
        .id(assignmentId)
        .status(assignment.getStatus())
        .build();
  }

  @Override
  public AssignmentDetailForEditResponse getAssignmentDetailForEdit(int assignmentId) {
    String username = jwtService.extractUsername();
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "User Not Found"));

    if (!"ADMIN".equalsIgnoreCase(user.getRole().getName())) {
      throw new AppException(HttpStatus.FORBIDDEN, "Only admins can access this endpoint");
    }

    Assignment assignment =
        assignmentRepository
            .findByIdAndDeletedFalse(assignmentId)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Assignment not found"));

    if (!assignment.getAsset().getLocation().getId().equals(user.getLocation().getId())
        || !assignment.getAssignedTo().getLocation().getId().equals(user.getLocation().getId())) {
      throw new AppException(HttpStatus.FORBIDDEN, "Assignment not in admin's location");
    }

    return assignmentMapper.toDetailForEditDto(assignment);
  }
}
