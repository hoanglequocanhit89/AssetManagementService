package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.dto.request.assignment.CreateUpdateAssignmentRequest;
import com.rookie.asset_management.dto.response.assignment.AssignmentListDtoResponse;
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
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
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
}
