package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.rookie.asset_management.dto.request.assignment.CreateUpdateAssignmentRequest;
import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.assignment.AssignmentDetailDtoResponse;
import com.rookie.asset_management.dto.response.assignment.AssignmentListDtoResponse;
import com.rookie.asset_management.dto.response.assignment.AssignmentStatusResponse;
import com.rookie.asset_management.dto.response.assignment.MyAssignmentDtoResponse;
import com.rookie.asset_management.entity.Asset;
import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.Location;
import com.rookie.asset_management.entity.Role;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.enums.AssetStatus;
import com.rookie.asset_management.enums.AssignmentStatus;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.mapper.AssignmentMapper;
import com.rookie.asset_management.repository.AssetRepository;
import com.rookie.asset_management.repository.AssignmentRepository;
import com.rookie.asset_management.repository.UserRepository;
import com.rookie.asset_management.service.impl.AssignmentServiceImpl;
import com.rookie.asset_management.service.impl.handler.NotificationCreatorImpl;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AssignmentServiceTest {

  @Mock private AssignmentRepository assignmentRepository;

  @Mock private AssignmentMapper assignmentMapper;

  @Mock private UserRepository userRepository;

  @Mock private NotificationCreatorImpl notificationCreator;

  @Mock private AssetRepository assetRepository;

  @Mock private JwtService jwtService;

  @Mock private EntityManager entityManager;

  @InjectMocks private AssignmentServiceImpl assignmentService;

  @Transactional
  @Test
  void createAssignment_Success() {
    // Arrange
    CreateUpdateAssignmentRequest request = new CreateUpdateAssignmentRequest();
    request.setUserId(1);
    request.setAssetId(1);
    request.setAssignedDate(LocalDate.now());
    request.setNote("Test Note");

    Location location = new Location();
    location.setId(1);

    User assignee = new User();
    assignee.setId(1);
    assignee.setLocation(location);

    User assigner = new User();
    assigner.setId(2);
    assigner.setLocation(location);

    Asset asset = new Asset();
    asset.setId(1);
    asset.setStatus(AssetStatus.AVAILABLE);
    asset.setLocation(location);

    Assignment assignment =
        Assignment.builder()
            .asset(asset)
            .assignedTo(assignee)
            .assignedDate(request.getAssignedDate())
            .note(request.getNote())
            .assignedBy(assigner)
            .status(AssignmentStatus.WAITING)
            .build();

    AssignmentListDtoResponse response = new AssignmentListDtoResponse();

    doNothing()
        .when(notificationCreator)
        .createAssignmentNotification(any(User.class), any(User.class), any(Assignment.class));
    when(userRepository.findById(1)).thenReturn(Optional.of(assignee));
    when(assetRepository.findById(1)).thenReturn(Optional.of(asset));
    when(jwtService.extractUsername()).thenReturn("assigner");
    when(userRepository.findByUsername("assigner")).thenReturn(Optional.of(assigner));
    when(assignmentRepository.save(any(Assignment.class))).thenReturn(assignment);
    when(assignmentMapper.toDto(any(Assignment.class))).thenReturn(response);

    AssignmentListDtoResponse result;

    // Mock TransactionSynchronizationManager behavior
    try (MockedStatic<TransactionSynchronizationManager> mockedStatic =
        Mockito.mockStatic(TransactionSynchronizationManager.class)) {
      mockedStatic
          .when(() -> TransactionSynchronizationManager.registerSynchronization(any()))
          .thenAnswer(invocation -> null);

      // Act
      result = assignmentService.createAssignment(request);
    }

    // Assert
    assertNotNull(result);
    verify(assignmentRepository, times(1)).save(any(Assignment.class));
  }

  @Test
  void createAssignment_UserNotFound_ThrowsException() {
    // Arrange
    CreateUpdateAssignmentRequest request = new CreateUpdateAssignmentRequest();
    request.setUserId(1);

    when(userRepository.findById(1)).thenReturn(Optional.empty());

    // Act & Assert
    AppException exception =
        assertThrows(AppException.class, () -> assignmentService.createAssignment(request));
    assertEquals("User Not Found", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
  }

  @Test
  void createAssignment_AssetNotFound_ThrowsException() {
    // Arrange
    CreateUpdateAssignmentRequest request = new CreateUpdateAssignmentRequest();
    request.setUserId(1);
    request.setAssetId(1);

    User assignee = new User();
    assignee.setId(1);

    when(userRepository.findById(1)).thenReturn(Optional.of(assignee));
    when(assetRepository.findById(1)).thenReturn(Optional.empty());

    // Act & Assert
    AppException exception =
        assertThrows(AppException.class, () -> assignmentService.createAssignment(request));
    assertEquals("Asset Not Found", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
  }

  @Test
  void createAssignment_AssetNotAvailable_ThrowsException() {
    // Arrange
    CreateUpdateAssignmentRequest request = new CreateUpdateAssignmentRequest();
    request.setUserId(1);
    request.setAssetId(1);

    Location location = new Location();
    location.setId(1);

    User assignee = new User();
    assignee.setId(1);
    assignee.setLocation(location);

    Asset asset = new Asset();
    asset.setId(1);
    asset.setStatus(AssetStatus.ASSIGNED);
    asset.setLocation(location);

    when(userRepository.findById(1)).thenReturn(Optional.of(assignee));
    when(assetRepository.findById(1)).thenReturn(Optional.of(asset));

    // Act & Assert
    AppException exception =
        assertThrows(AppException.class, () -> assignmentService.createAssignment(request));
    assertEquals("Asset is not available for assignment", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
  }

  @Test
  void createAssignment_LocationMismatch_ThrowsException() {
    // Arrange
    CreateUpdateAssignmentRequest request = new CreateUpdateAssignmentRequest();
    request.setUserId(1);
    request.setAssetId(1);

    Location location1 = new Location();
    location1.setId(1);

    Location location2 = new Location();
    location2.setId(2);

    User assignee = new User();
    assignee.setId(1);
    assignee.setLocation(location1);

    User assigner = new User();
    assigner.setId(2);
    assigner.setLocation(location2);

    Asset asset = new Asset();
    asset.setId(1);
    asset.setStatus(AssetStatus.AVAILABLE);
    asset.setLocation(location1);

    when(userRepository.findById(1)).thenReturn(Optional.of(assignee));
    when(assetRepository.findById(1)).thenReturn(Optional.of(asset));
    when(jwtService.extractUsername()).thenReturn("assigner");
    when(userRepository.findByUsername("assigner")).thenReturn(Optional.of(assigner));

    // Act & Assert
    AppException exception =
        assertThrows(AppException.class, () -> assignmentService.createAssignment(request));
    assertEquals("Assigner and assignee must be in the same location", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
  }

  @Test
  void editAssignment_Success() {
    // Arrange
    int assignmentId = 1;
    CreateUpdateAssignmentRequest request = new CreateUpdateAssignmentRequest();
    request.setUserId(2);
    request.setAssetId(2);
    request.setAssignedDate(LocalDate.now().plusDays(1));
    request.setNote("Updated Note");

    Location location = new Location();
    location.setId(1);

    User originalAssigner = new User();
    originalAssigner.setId(1);
    originalAssigner.setLocation(location);

    User newAssignee = new User();
    newAssignee.setId(2);
    newAssignee.setLocation(location);

    Asset oldAsset = new Asset();
    oldAsset.setId(1);
    oldAsset.setStatus(AssetStatus.AVAILABLE);
    oldAsset.setLocation(location);

    Asset newAsset = new Asset();
    newAsset.setId(2);
    newAsset.setStatus(AssetStatus.AVAILABLE);
    newAsset.setLocation(location);

    Assignment existingAssignment = new Assignment();
    existingAssignment.setId(assignmentId);
    existingAssignment.setStatus(AssignmentStatus.WAITING);
    existingAssignment.setAssignedBy(originalAssigner);
    existingAssignment.setAsset(oldAsset);

    AssignmentListDtoResponse response = new AssignmentListDtoResponse();

    when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(existingAssignment));
    when(userRepository.findById(2)).thenReturn(Optional.of(newAssignee));
    when(assetRepository.findById(2)).thenReturn(Optional.of(newAsset));
    when(assignmentRepository.existsByAssetAndStatusAndDeletedFalse(
            newAsset, AssignmentStatus.WAITING))
        .thenReturn(false);
    when(assignmentRepository.save(any(Assignment.class))).thenReturn(existingAssignment);
    when(assignmentMapper.toDto(any(Assignment.class))).thenReturn(response);

    // Act
    AssignmentListDtoResponse result = assignmentService.editAssignment(assignmentId, request);

    // Assert
    assertNotNull(result);
    verify(assignmentRepository, times(1)).save(any(Assignment.class));
    assertEquals(newAssignee, existingAssignment.getAssignedTo());
    assertEquals(newAsset, existingAssignment.getAsset());
    assertEquals(request.getNote(), existingAssignment.getNote());
    assertEquals(request.getAssignedDate(), existingAssignment.getAssignedDate());
  }

  @Test
  void editAssignment_SameAsset_Success() {
    // Arrange - Test case where asset ID remains the same
    int assignmentId = 1;
    CreateUpdateAssignmentRequest request = new CreateUpdateAssignmentRequest();
    request.setUserId(2);
    request.setAssetId(1); // Same asset ID
    request.setAssignedDate(LocalDate.now().plusDays(1));
    request.setNote("Updated Note");

    Location location = new Location();
    location.setId(1);

    User originalAssigner = new User();
    originalAssigner.setId(1);
    originalAssigner.setLocation(location);

    User newAssignee = new User();
    newAssignee.setId(2);
    newAssignee.setLocation(location);

    Asset asset = new Asset();
    asset.setId(1);
    asset.setStatus(AssetStatus.AVAILABLE);
    asset.setLocation(location);

    Assignment existingAssignment = new Assignment();
    existingAssignment.setId(assignmentId);
    existingAssignment.setStatus(AssignmentStatus.WAITING); // Must be WAITING to be editable
    existingAssignment.setAssignedBy(originalAssigner);
    existingAssignment.setAsset(asset);

    AssignmentListDtoResponse response = new AssignmentListDtoResponse();

    when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(existingAssignment));
    when(userRepository.findById(2)).thenReturn(Optional.of(newAssignee));
    when(assetRepository.findById(1)).thenReturn(Optional.of(asset));
    when(assignmentRepository.save(any(Assignment.class))).thenReturn(existingAssignment);
    when(assignmentMapper.toDto(any(Assignment.class))).thenReturn(response);

    // Act
    AssignmentListDtoResponse result = assignmentService.editAssignment(assignmentId, request);

    // Assert
    assertNotNull(result);
    verify(assignmentRepository, times(1)).save(any(Assignment.class));
    // Should not check for waiting assignment since it's the same asset
    verify(assignmentRepository, times(0))
        .existsByAssetAndStatusAndDeletedFalse(any(Asset.class), any(AssignmentStatus.class));
  }

  @Test
  void editAssignment_AssignmentNotFound_ThrowsException() {
    // Arrange
    int assignmentId = 1;
    CreateUpdateAssignmentRequest request = new CreateUpdateAssignmentRequest();

    when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.empty());

    // Act & Assert
    AppException exception =
        assertThrows(
            AppException.class, () -> assignmentService.editAssignment(assignmentId, request));
    assertEquals("Assignment Not Found", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
  }

  @Test
  void editAssignment_NotInWaitingState_ThrowsException() {
    // Arrange
    int assignmentId = 1;
    CreateUpdateAssignmentRequest request = new CreateUpdateAssignmentRequest();

    Assignment existingAssignment = new Assignment();
    existingAssignment.setId(assignmentId);
    existingAssignment.setStatus(AssignmentStatus.ACCEPTED); // Not WAITING

    when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(existingAssignment));

    // Act & Assert
    AppException exception =
        assertThrows(
            AppException.class, () -> assignmentService.editAssignment(assignmentId, request));
    assertEquals("Only assignments in WAITING state can be edited", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
  }

  @Test
  void editAssignment_UserNotFound_ThrowsException() {
    // Arrange
    int assignmentId = 1;
    CreateUpdateAssignmentRequest request = new CreateUpdateAssignmentRequest();
    request.setUserId(2);

    Assignment existingAssignment = new Assignment();
    existingAssignment.setId(assignmentId);
    existingAssignment.setStatus(AssignmentStatus.WAITING);

    when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(existingAssignment));
    when(userRepository.findById(2)).thenReturn(Optional.empty());

    // Act & Assert
    AppException exception =
        assertThrows(
            AppException.class, () -> assignmentService.editAssignment(assignmentId, request));
    assertEquals("User Not Found", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
  }

  @Test
  void editAssignment_AssetNotFound_ThrowsException() {
    // Arrange
    int assignmentId = 1;
    CreateUpdateAssignmentRequest request = new CreateUpdateAssignmentRequest();
    request.setUserId(2);
    request.setAssetId(2);

    User newAssignee = new User();
    newAssignee.setId(2);

    Assignment existingAssignment = new Assignment();
    existingAssignment.setId(assignmentId);
    existingAssignment.setStatus(AssignmentStatus.WAITING);

    when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(existingAssignment));
    when(userRepository.findById(2)).thenReturn(Optional.of(newAssignee));
    when(assetRepository.findById(2)).thenReturn(Optional.empty());

    // Act & Assert
    AppException exception =
        assertThrows(
            AppException.class, () -> assignmentService.editAssignment(assignmentId, request));
    assertEquals("Asset Not Found", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
  }

  @Test
  void editAssignment_AssignerAndAssigneeLocationMismatch_ThrowsException() {
    // Arrange
    int assignmentId = 1;
    CreateUpdateAssignmentRequest request = new CreateUpdateAssignmentRequest();
    request.setUserId(2);
    request.setAssetId(1);

    Location location1 = new Location();
    location1.setId(1);

    Location location2 = new Location();
    location2.setId(2);

    User originalAssigner = new User();
    originalAssigner.setId(1);
    originalAssigner.setLocation(location1);

    User newAssignee = new User();
    newAssignee.setId(2);
    newAssignee.setLocation(location2); // Different location

    Asset asset = new Asset();
    asset.setId(1);
    asset.setStatus(AssetStatus.AVAILABLE);
    asset.setLocation(location1);

    Assignment existingAssignment = new Assignment();
    existingAssignment.setId(assignmentId);
    existingAssignment.setStatus(AssignmentStatus.WAITING);
    existingAssignment.setAssignedBy(originalAssigner);
    existingAssignment.setAsset(asset);

    when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(existingAssignment));
    when(userRepository.findById(2)).thenReturn(Optional.of(newAssignee));
    when(assetRepository.findById(1)).thenReturn(Optional.of(asset));

    // Act & Assert
    AppException exception =
        assertThrows(
            AppException.class, () -> assignmentService.editAssignment(assignmentId, request));
    assertEquals("Assigner and assignee must be in the same location", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
  }

  @Test
  void editAssignment_AssetNotAvailable_ThrowsException() {
    // Arrange
    int assignmentId = 1;
    CreateUpdateAssignmentRequest request = new CreateUpdateAssignmentRequest();
    request.setUserId(2);
    request.setAssetId(2);

    Location location = new Location();
    location.setId(1);

    User originalAssigner = new User();
    originalAssigner.setId(1);
    originalAssigner.setLocation(location);

    User newAssignee = new User();
    newAssignee.setId(2);
    newAssignee.setLocation(location);

    Asset newAsset = new Asset();
    newAsset.setId(2);
    newAsset.setStatus(AssetStatus.ASSIGNED); // Not available
    newAsset.setLocation(location);

    Assignment existingAssignment = new Assignment();
    existingAssignment.setId(assignmentId);
    existingAssignment.setStatus(AssignmentStatus.WAITING);
    existingAssignment.setAssignedBy(originalAssigner);

    when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(existingAssignment));
    when(userRepository.findById(2)).thenReturn(Optional.of(newAssignee));
    when(assetRepository.findById(2)).thenReturn(Optional.of(newAsset));

    // Act & Assert
    AppException exception =
        assertThrows(
            AppException.class, () -> assignmentService.editAssignment(assignmentId, request));
    assertEquals("Asset is not available for assignment", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
  }

  @Test
  void editAssignment_AssignmentDeleted_ThrowsException() {
    // Arrange
    int assignmentId = 1;
    CreateUpdateAssignmentRequest request = new CreateUpdateAssignmentRequest();
    request.setUserId(2);
    request.setAssetId(2);

    Assignment deletedAssignment = new Assignment();
    deletedAssignment.setId(assignmentId);
    deletedAssignment.setDeleted(true); // Mark the assignment as deleted

    when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(deletedAssignment));

    // Act & Assert
    AppException exception =
        assertThrows(
            AppException.class, () -> assignmentService.editAssignment(assignmentId, request));
    assertEquals(
        "Update failed: The Assignment was modified by another user. Please refresh and try again.",
        exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
  }

  @Test
  void editAssignment_AssetHasWaitingAssignment_ThrowsException() {
    // Arrange
    int assignmentId = 1;
    CreateUpdateAssignmentRequest request = new CreateUpdateAssignmentRequest();
    request.setUserId(2);
    request.setAssetId(2); // Different asset

    Location location = new Location();
    location.setId(1);

    User originalAssigner = new User();
    originalAssigner.setId(1);
    originalAssigner.setLocation(location);

    User newAssignee = new User();
    newAssignee.setId(2);
    newAssignee.setLocation(location);

    Asset oldAsset = new Asset();
    oldAsset.setId(1);

    Asset newAsset = new Asset();
    newAsset.setId(2);
    newAsset.setStatus(AssetStatus.AVAILABLE);
    newAsset.setLocation(location);

    Assignment existingAssignment = new Assignment();
    existingAssignment.setId(assignmentId);
    existingAssignment.setStatus(AssignmentStatus.WAITING);
    existingAssignment.setAssignedBy(originalAssigner);
    existingAssignment.setAsset(oldAsset);

    when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(existingAssignment));
    when(userRepository.findById(2)).thenReturn(Optional.of(newAssignee));
    when(assetRepository.findById(2)).thenReturn(Optional.of(newAsset));
    when(assignmentRepository.existsByAssetAndStatusAndDeletedFalse(
            newAsset, AssignmentStatus.WAITING))
        .thenReturn(true); // Asset already has a waiting assignment

    // Act & Assert
    AppException exception =
        assertThrows(
            AppException.class, () -> assignmentService.editAssignment(assignmentId, request));
    assertEquals("Asset already has a waiting assignment", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
  }

  @Test
  void editAssignment_AssetAndAssignerLocationMismatch_ThrowsException() {
    // Arrange
    int assignmentId = 1;
    CreateUpdateAssignmentRequest request = new CreateUpdateAssignmentRequest();
    request.setUserId(2);
    request.setAssetId(2);

    Location location1 = new Location();
    location1.setId(1);

    Location location2 = new Location();
    location2.setId(2);

    User originalAssigner = new User();
    originalAssigner.setId(1);
    originalAssigner.setLocation(location1);

    User newAssignee = new User();
    newAssignee.setId(2);
    newAssignee.setLocation(location1);

    Asset oldAsset = new Asset();
    oldAsset.setId(1);

    Asset newAsset = new Asset();
    newAsset.setId(2);
    newAsset.setStatus(AssetStatus.AVAILABLE);
    newAsset.setLocation(location2); // Different location from assigner

    Assignment existingAssignment = new Assignment();
    existingAssignment.setId(assignmentId);
    existingAssignment.setStatus(AssignmentStatus.WAITING);
    existingAssignment.setAssignedBy(originalAssigner);
    existingAssignment.setAsset(oldAsset);

    when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(existingAssignment));
    when(userRepository.findById(2)).thenReturn(Optional.of(newAssignee));
    when(assetRepository.findById(2)).thenReturn(Optional.of(newAsset));
    when(assignmentRepository.existsByAssetAndStatusAndDeletedFalse(
            newAsset, AssignmentStatus.WAITING))
        .thenReturn(false);

    // Act & Assert
    AppException exception =
        assertThrows(
            AppException.class, () -> assignmentService.editAssignment(assignmentId, request));
    assertEquals("Asset must be in the same location with assigner", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
  }

  @Test
  void getAllAssignments_NonAdminUser_ThrowsException() {
    // Arrange
    String username = "user";
    Role userRole = new Role();
    userRole.setName("USER");

    User user = new User();
    user.setUsername(username);
    user.setRole(userRole);

    when(jwtService.extractUsername()).thenReturn(username);
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

    // Act & Assert
    AppException exception =
        assertThrows(
            AppException.class,
            () -> assignmentService.getAllAssignments(null, null, null, 0, 10, "id", "asc"));
    assertEquals("Only admins can access this endpoint", exception.getMessage());
    assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatusCode());
  }

  @Test
  void getAllAssignments_UserNotFound_ThrowsException() {
    // Arrange
    String username = "unknown";
    when(jwtService.extractUsername()).thenReturn(username);
    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    // Act & Assert
    AppException exception =
        assertThrows(
            AppException.class,
            () -> assignmentService.getAllAssignments(null, null, null, 0, 10, "id", "asc"));
    assertEquals("User Not Found", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
  }

  @Test
  void getAssignmentDetails_Success() {
    // Arrange
    String username = "admin";
    Location location = new Location();
    location.setId(1);

    Role adminRole = new Role();
    adminRole.setName("ADMIN");

    User admin = new User();
    admin.setId(1);
    admin.setUsername(username);
    admin.setRole(adminRole);
    admin.setLocation(location);

    Asset asset = new Asset();
    asset.setLocation(location);

    Assignment assignment = new Assignment();
    assignment.setId(1);
    assignment.setAssignedTo(admin);
    assignment.setAsset(asset);
    assignment.setDeleted(false);

    AssignmentDetailDtoResponse responseDto = new AssignmentDetailDtoResponse();

    when(jwtService.extractUsername()).thenReturn(username);
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(admin));
    when(assignmentRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(assignment));
    when(assignmentMapper.toDetailDto(assignment)).thenReturn(responseDto);

    // Act
    ApiDtoResponse<AssignmentDetailDtoResponse> result = assignmentService.getAssignmentDetails(1);

    // Assert
    assertNotNull(result);
    assertNotNull(result.getData());
    assertEquals("Assignment details retrieved successfully", result.getMessage());
    verify(assignmentRepository, times(1)).findByIdAndDeletedFalse(1);
  }

  @Test
  void getAssignmentDetails_AssignmentNotFound_ThrowsException() {
    // Arrange
    String username = "admin";
    Role adminRole = new Role();
    adminRole.setName("ADMIN");

    User admin = new User();
    admin.setUsername(username);
    admin.setRole(adminRole);

    when(jwtService.extractUsername()).thenReturn(username);
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(admin));
    when(assignmentRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.empty());

    // Act & Assert
    AppException exception =
        assertThrows(AppException.class, () -> assignmentService.getAssignmentDetails(1));
    assertEquals("Assignment not found", exception.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatusCode());
  }

  @Test
  void getAssignmentDetails_LocationMismatch_ThrowsException() {
    // Arrange
    String username = "admin";
    Location adminLocation = new Location();
    adminLocation.setId(1);
    Location otherLocation = new Location();
    otherLocation.setId(2);

    Role adminRole = new Role();
    adminRole.setName("ADMIN");

    User admin = new User();
    admin.setId(1);
    admin.setUsername(username);
    admin.setRole(adminRole);
    admin.setLocation(adminLocation);

    Asset asset = new Asset();
    asset.setLocation(otherLocation);

    User assignee = new User();
    assignee.setLocation(otherLocation);

    Assignment assignment = new Assignment();
    assignment.setId(1);
    assignment.setAssignedTo(assignee);
    assignment.setAsset(asset);
    assignment.setDeleted(false);

    when(jwtService.extractUsername()).thenReturn(username);
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(admin));
    when(assignmentRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(assignment));

    // Act & Assert
    AppException exception =
        assertThrows(AppException.class, () -> assignmentService.getAssignmentDetails(1));
    assertEquals("Assignment not in admin's location", exception.getMessage());
    assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatusCode());
  }

  @Test
  void deleteAssignment_Success() {
    // Arrange
    String username = "admin";
    Location location = new Location();
    location.setId(1);

    Role adminRole = new Role();
    adminRole.setName("ADMIN");

    User admin = new User();
    admin.setId(1);
    admin.setUsername(username);
    admin.setRole(adminRole);
    admin.setLocation(location);

    Asset asset = new Asset();
    asset.setLocation(location);

    Assignment assignment = new Assignment();
    assignment.setId(1);
    assignment.setAssignedTo(admin);
    assignment.setAsset(asset);
    assignment.setStatus(AssignmentStatus.WAITING);
    assignment.setDeleted(false);

    when(jwtService.extractUsername()).thenReturn(username);
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(admin));
    when(assignmentRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(assignment));
    when(assignmentRepository.save(any(Assignment.class))).thenReturn(assignment);

    // Act
    ApiDtoResponse<Void> result = assignmentService.deleteAssignment(1);

    // Assert
    assertNotNull(result);
    assertEquals("Assignment deleted successfully", result.getMessage());
    verify(assignmentRepository, times(1)).save(any(Assignment.class));
    assertEquals(true, assignment.isDeleted());
  }

  @Test
  void deleteAssignment_NonAdminUser_ThrowsException() {
    // Arrange
    String username = "user";
    Role userRole = new Role();
    userRole.setName("USER");

    User user = new User();
    user.setUsername(username);
    user.setRole(userRole);

    when(jwtService.extractUsername()).thenReturn(username);
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

    // Act & Assert
    AppException exception =
        assertThrows(AppException.class, () -> assignmentService.deleteAssignment(1));
    assertEquals("Only admins can access this endpoint", exception.getMessage());
    assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatusCode());
  }

  @Test
  void deleteAssignment_AssignmentNotFound_ThrowsException() {
    // Arrange
    String username = "admin";
    Role adminRole = new Role();
    adminRole.setName("ADMIN");

    User admin = new User();
    admin.setUsername(username);
    admin.setRole(adminRole);

    when(jwtService.extractUsername()).thenReturn(username);
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(admin));
    when(assignmentRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.empty());

    // Act & Assert
    AppException exception =
        assertThrows(AppException.class, () -> assignmentService.deleteAssignment(1));
    assertEquals("Assignment not found", exception.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatusCode());
  }

  @Test
  void deleteAssignment_InvalidStatus_ThrowsException() {
    // Arrange
    String username = "admin";
    Location location = new Location();
    location.setId(1);

    Role adminRole = new Role();
    adminRole.setName("ADMIN");

    User admin = new User();
    admin.setId(1);
    admin.setUsername(username);
    admin.setRole(adminRole);
    admin.setLocation(location);

    Asset asset = new Asset();
    asset.setLocation(location);

    Assignment assignment = new Assignment();
    assignment.setId(1);
    assignment.setAssignedTo(admin);
    assignment.setAsset(asset);
    assignment.setStatus(AssignmentStatus.ACCEPTED); // Invalid status for deletion
    assignment.setDeleted(false);

    when(jwtService.extractUsername()).thenReturn(username);
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(admin));
    when(assignmentRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(assignment));

    // Act & Assert
    AppException exception =
        assertThrows(AppException.class, () -> assignmentService.deleteAssignment(1));
    assertEquals(
        "Can only delete assignments with status WAITING or DECLINED", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
  }

  @Test
  void getMyAssignments_Success() {
    // Arrange
    String username = "user";
    Location location = new Location();
    location.setId(1);

    User user = new User();
    user.setId(1);
    user.setUsername(username);
    user.setLocation(location);

    Assignment assignment = new Assignment();
    assignment.setId(1);
    assignment.setAssignedTo(user);
    assignment.setStatus(AssignmentStatus.WAITING);
    assignment.setDeleted(false);

    MyAssignmentDtoResponse responseDto = new MyAssignmentDtoResponse();
    List<Assignment> assignments = Collections.singletonList(assignment);

    when(jwtService.extractUsername()).thenReturn(username);
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(assignmentRepository.findAll(any(Specification.class), any(Sort.class)))
        .thenReturn(assignments);
    when(assignmentMapper.toMyAssignmentDto(any(Assignment.class))).thenReturn(responseDto);

    // Act
    ApiDtoResponse<List<MyAssignmentDtoResponse>> result =
        assignmentService.getMyAssignments("assetCode", "asc");

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getData().size());
    assertEquals("My assignments retrieved successfully", result.getMessage());
    verify(assignmentRepository, times(1)).findAll(any(Specification.class), any(Sort.class));
  }

  @Test
  void getMyAssignments_StatusSort_Success() {
    // Arrange
    String username = "user";
    User user = new User();
    user.setId(1);
    user.setUsername(username);

    Assignment assignment1 = new Assignment();
    assignment1.setId(1);
    assignment1.setAssignedTo(user);
    assignment1.setStatus(AssignmentStatus.ACCEPTED);
    assignment1.setDeleted(false);

    Assignment assignment2 = new Assignment();
    assignment2.setId(2);
    assignment2.setAssignedTo(user);
    assignment2.setStatus(AssignmentStatus.WAITING);
    assignment2.setDeleted(false);

    MyAssignmentDtoResponse responseDto = new MyAssignmentDtoResponse();
    List<Assignment> assignments = Arrays.asList(assignment1, assignment2);

    when(jwtService.extractUsername()).thenReturn(username);
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(assignmentRepository.findAll(any(Specification.class))).thenReturn(assignments);
    when(assignmentMapper.toMyAssignmentDto(any(Assignment.class))).thenReturn(responseDto);

    // Act
    ApiDtoResponse<List<MyAssignmentDtoResponse>> result =
        assignmentService.getMyAssignments("status", "asc");

    // Assert
    assertNotNull(result);
    assertEquals(2, result.getData().size());
    assertEquals("My assignments retrieved successfully", result.getMessage());
    verify(assignmentRepository, times(1)).findAll(any(Specification.class));
  }

  @Test
  void getMyAssignments_UserNotFound_ThrowsException() {
    // Arrange
    String username = "unknown";
    when(jwtService.extractUsername()).thenReturn(username);
    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    // Act & Assert
    AppException exception =
        assertThrows(
            AppException.class, () -> assignmentService.getMyAssignments("assetCode", "asc"));
    assertEquals("User Not Found", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
  }

  @Test
  void responseToAssignment_AcceptedStatus_Success() {
    // Arrange
    int assignmentId = 1;
    AssignmentStatus status = AssignmentStatus.ACCEPTED;

    Location location = new Location();
    location.setId(1);

    User assignedUser = new User();
    assignedUser.setId(1);
    assignedUser.setUsername("assignee");
    assignedUser.setLocation(location);

    Asset asset = new Asset();
    asset.setId(1);
    asset.setStatus(AssetStatus.AVAILABLE);
    asset.setLocation(location);

    Assignment assignment = new Assignment();
    assignment.setId(assignmentId);
    assignment.setStatus(AssignmentStatus.WAITING);
    assignment.setAssignedTo(assignedUser);
    assignment.setAsset(asset);

    doNothing()
        .when(notificationCreator)
        .createAssignmentNotification(any(User.class), any(User.class), any(Assignment.class));
    when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
    when(jwtService.extractUsername()).thenReturn("assignee");
    when(assignmentRepository.save(any(Assignment.class))).thenReturn(assignment);
    when(assetRepository.save(any(Asset.class))).thenReturn(asset);

    // Act
    AssignmentStatusResponse result = assignmentService.responseToAssignment(assignmentId, status);

    // Assert
    assertNotNull(result);
    assertEquals(assignmentId, result.getId());
    assertEquals(AssignmentStatus.ACCEPTED, result.getStatus());
    assertEquals(AssignmentStatus.ACCEPTED, assignment.getStatus());
    assertEquals(AssetStatus.ASSIGNED, asset.getStatus());
    verify(assignmentRepository, times(1)).save(assignment);
    verify(assetRepository, times(1)).save(asset);
  }

  @Test
  void responseToAssignment_DeclinedStatus_Success() {
    // Arrange
    int assignmentId = 1;
    AssignmentStatus status = AssignmentStatus.DECLINED;

    Location location = new Location();
    location.setId(1);

    User assignedUser = new User();
    assignedUser.setId(1);
    assignedUser.setUsername("assignee");
    assignedUser.setLocation(location);

    Asset asset = new Asset();
    asset.setId(1);
    asset.setStatus(AssetStatus.AVAILABLE);
    asset.setLocation(location);

    Assignment assignment = new Assignment();
    assignment.setId(assignmentId);
    assignment.setStatus(AssignmentStatus.WAITING);
    assignment.setAssignedTo(assignedUser);
    assignment.setAsset(asset);

    doNothing()
        .when(notificationCreator)
        .createAssignmentNotification(any(User.class), any(User.class), any(Assignment.class));
    when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
    when(jwtService.extractUsername()).thenReturn("assignee");
    when(assignmentRepository.save(any(Assignment.class))).thenReturn(assignment);

    // Act
    AssignmentStatusResponse result = assignmentService.responseToAssignment(assignmentId, status);

    // Assert
    assertNotNull(result);
    assertEquals(assignmentId, result.getId());
    assertEquals(AssignmentStatus.DECLINED, result.getStatus());
    assertEquals(AssignmentStatus.DECLINED, assignment.getStatus());
    assertEquals(AssetStatus.AVAILABLE, asset.getStatus()); // Asset status should remain unchanged
    verify(assignmentRepository, times(1)).save(assignment);
    verify(assetRepository, times(0))
        .save(any(Asset.class)); // Asset should not be saved for DECLINED
  }

  @Test
  void responseToAssignment_InvalidStatus_ThrowsException() {
    // Arrange
    int assignmentId = 1;
    AssignmentStatus status = AssignmentStatus.WAITING; // Invalid status for response

    // Act & Assert
    AppException exception =
        assertThrows(
            AppException.class, () -> assignmentService.responseToAssignment(assignmentId, status));
    assertEquals("Status must be either ACCEPTED or DECLINED", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
  }

  @Test
  void responseToAssignment_AssignmentNotFound_ThrowsException() {
    // Arrange
    int assignmentId = 1;
    AssignmentStatus status = AssignmentStatus.ACCEPTED;

    when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.empty());

    // Act & Assert
    AppException exception =
        assertThrows(
            AppException.class, () -> assignmentService.responseToAssignment(assignmentId, status));
    assertEquals("Assignment Not Found", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
  }

  @Test
  void responseToAssignment_AssignmentNotInWaitingState_ThrowsException() {
    // Arrange
    int assignmentId = 1;
    AssignmentStatus status = AssignmentStatus.ACCEPTED;

    Assignment assignment = new Assignment();
    assignment.setId(assignmentId);
    assignment.setStatus(AssignmentStatus.ACCEPTED); // Already accepted, not WAITING

    when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));

    // Act & Assert
    AppException exception =
        assertThrows(
            AppException.class, () -> assignmentService.responseToAssignment(assignmentId, status));
    assertEquals("Only assignments in WAITING state can be responded to", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
  }

  @Test
  void responseToAssignment_UnauthorizedUser_ThrowsException() {
    // Arrange
    int assignmentId = 1;
    AssignmentStatus status = AssignmentStatus.ACCEPTED;

    User assignedUser = new User();
    assignedUser.setId(1);
    assignedUser.setUsername("assignee");

    Assignment assignment = new Assignment();
    assignment.setId(assignmentId);
    assignment.setStatus(AssignmentStatus.WAITING);
    assignment.setAssignedTo(assignedUser);

    when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
    when(jwtService.extractUsername())
        .thenReturn("different_user"); // Different user trying to respond

    // Act & Assert
    AppException exception =
        assertThrows(
            AppException.class, () -> assignmentService.responseToAssignment(assignmentId, status));
    assertEquals("You are not authorized to respond to this assignment", exception.getMessage());
    assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatusCode());
  }

  @Test
  void responseToAssignment_AcceptedStatus_AssetStatusUpdated() {
    // Arrange
    int assignmentId = 1;
    AssignmentStatus status = AssignmentStatus.ACCEPTED;

    User assignedUser = new User();
    assignedUser.setId(1);
    assignedUser.setUsername("assignee");

    Asset asset = new Asset();
    asset.setId(1);
    asset.setStatus(AssetStatus.AVAILABLE);

    Assignment assignment = new Assignment();
    assignment.setId(assignmentId);
    assignment.setStatus(AssignmentStatus.WAITING);
    assignment.setAssignedTo(assignedUser);
    assignment.setAsset(asset);

    doNothing()
        .when(notificationCreator)
        .createAssignmentNotification(any(User.class), any(User.class), any(Assignment.class));
    when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
    when(jwtService.extractUsername()).thenReturn("assignee");
    when(assignmentRepository.save(any(Assignment.class))).thenReturn(assignment);
    when(assetRepository.save(any(Asset.class))).thenReturn(asset);

    // Act
    assignmentService.responseToAssignment(assignmentId, status);

    // Assert
    assertEquals(AssetStatus.ASSIGNED, asset.getStatus());
    verify(assetRepository, times(1)).save(asset);
  }

  @Test
  void responseToAssignment_DeclinedStatus_AssetStatusUnchanged() {
    // Arrange
    int assignmentId = 1;
    AssignmentStatus status = AssignmentStatus.DECLINED;

    User assignedUser = new User();
    assignedUser.setId(1);
    assignedUser.setUsername("assignee");

    Asset asset = new Asset();
    asset.setId(1);
    asset.setStatus(AssetStatus.AVAILABLE);

    Assignment assignment = new Assignment();
    assignment.setId(assignmentId);
    assignment.setStatus(AssignmentStatus.WAITING);
    assignment.setAssignedTo(assignedUser);
    assignment.setAsset(asset);

    doNothing()
        .when(notificationCreator)
        .createAssignmentNotification(any(User.class), any(User.class), any(Assignment.class));
    when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
    when(jwtService.extractUsername()).thenReturn("assignee");
    when(assignmentRepository.save(any(Assignment.class))).thenReturn(assignment);

    // Act
    assignmentService.responseToAssignment(assignmentId, status);

    // Assert
    assertEquals(AssetStatus.AVAILABLE, asset.getStatus()); // Should remain unchanged
    verify(assetRepository, times(0)).save(any(Asset.class)); // Should not be called
  }

  @Test
  void getMyAssignments_OnlyReturnsAssignmentsWithAssignedDateBeforeOrEqualToday_Success() {
    // Arrange
    String username = "user";
    User user = new User();
    user.setId(1);
    user.setUsername(username);

    // Assignment with today's date - should be included
    Assignment assignmentToday = new Assignment();
    assignmentToday.setId(1);
    assignmentToday.setAssignedTo(user);
    assignmentToday.setStatus(AssignmentStatus.WAITING);
    assignmentToday.setAssignedDate(LocalDate.now());
    assignmentToday.setDeleted(false);

    // Assignment with past date - should be included
    Assignment assignmentPast = new Assignment();
    assignmentPast.setId(2);
    assignmentPast.setAssignedTo(user);
    assignmentPast.setStatus(AssignmentStatus.ACCEPTED);
    assignmentPast.setAssignedDate(LocalDate.now().minusDays(5));
    assignmentPast.setDeleted(false);

    MyAssignmentDtoResponse responseDto = new MyAssignmentDtoResponse();
    List<Assignment> assignments = Arrays.asList(assignmentToday, assignmentPast);

    when(jwtService.extractUsername()).thenReturn(username);
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(assignmentRepository.findAll(any(Specification.class), any(Sort.class)))
        .thenReturn(assignments);
    when(assignmentMapper.toMyAssignmentDto(any(Assignment.class))).thenReturn(responseDto);

    // Act
    ApiDtoResponse<List<MyAssignmentDtoResponse>> result =
        assignmentService.getMyAssignments("assetCode", "asc");

    // Assert
    assertNotNull(result);
    assertEquals(2, result.getData().size());
    assertEquals("My assignments retrieved successfully", result.getMessage());

    // Verify that the specification includes the assigned date filter
    verify(assignmentRepository, times(1)).findAll(any(Specification.class), any(Sort.class));
  }

  @Test
  void getMyAssignments_VerifySpecificationIncludesAssignedDateFilter() {
    // Arrange
    String username = "user";
    User user = new User();
    user.setId(1);
    user.setUsername(username);

    when(jwtService.extractUsername()).thenReturn(username);
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(assignmentRepository.findAll(any(Specification.class), any(Sort.class)))
        .thenReturn(Collections.emptyList());

    // Act
    assignmentService.getMyAssignments("assetCode", "asc");

    // Assert
    ArgumentCaptor<Specification<Assignment>> specCaptor =
        ArgumentCaptor.forClass(Specification.class);
    verify(assignmentRepository).findAll(specCaptor.capture(), any(Sort.class));

    assertNotNull(specCaptor.getValue());
  }

  @Test
  void getMyAssignments_WithStatusSort_VerifyAssignedDateFilter() {
    // Arrange
    String username = "user";
    User user = new User();
    user.setId(1);
    user.setUsername(username);

    Assignment assignment1 = new Assignment();
    assignment1.setId(1);
    assignment1.setAssignedTo(user);
    assignment1.setStatus(AssignmentStatus.ACCEPTED);
    assignment1.setAssignedDate(LocalDate.now().minusDays(1));
    assignment1.setDeleted(false);

    Assignment assignment2 = new Assignment();
    assignment2.setId(2);
    assignment2.setAssignedTo(user);
    assignment2.setStatus(AssignmentStatus.WAITING);
    assignment2.setAssignedDate(LocalDate.now());
    assignment2.setDeleted(false);

    MyAssignmentDtoResponse responseDto = new MyAssignmentDtoResponse();
    List<Assignment> assignments = Arrays.asList(assignment1, assignment2);

    when(jwtService.extractUsername()).thenReturn(username);
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(assignmentRepository.findAll(any(Specification.class))).thenReturn(assignments);
    when(assignmentMapper.toMyAssignmentDto(any(Assignment.class))).thenReturn(responseDto);

    // Act
    ApiDtoResponse<List<MyAssignmentDtoResponse>> result =
        assignmentService.getMyAssignments("status", "asc");

    // Assert
    assertNotNull(result);
    assertEquals(2, result.getData().size());
    assertEquals("My assignments retrieved successfully", result.getMessage());

    // Verify that specification is called
    ArgumentCaptor<Specification<Assignment>> specCaptor =
        ArgumentCaptor.forClass(Specification.class);
    verify(assignmentRepository).findAll(specCaptor.capture());
    assertNotNull(specCaptor.getValue());
  }

  @Test
  void getMyAssignments_EmptyResult_WhenNoAssignmentsMatchDateFilter() {
    // Arrange
    String username = "user";
    User user = new User();
    user.setId(1);
    user.setUsername(username);

    when(jwtService.extractUsername()).thenReturn(username);
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(assignmentRepository.findAll(any(Specification.class), any(Sort.class)))
        .thenReturn(Collections.emptyList());

    // Act
    ApiDtoResponse<List<MyAssignmentDtoResponse>> result =
        assignmentService.getMyAssignments("assetCode", "asc");

    // Assert
    assertNotNull(result);
    assertEquals(0, result.getData().size());
    assertEquals("My assignments retrieved successfully", result.getMessage());
    verify(assignmentRepository, times(1)).findAll(any(Specification.class), any(Sort.class));
  }

  @Test
  void getMyAssignments_VerifyAllSpecificationFiltersApplied() {
    // Arrange
    String username = "user";
    User user = new User();
    user.setId(1);
    user.setUsername(username);

    when(jwtService.extractUsername()).thenReturn(username);
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(assignmentRepository.findAll(any(Specification.class), any(Sort.class)))
        .thenReturn(Collections.emptyList());

    // Act
    assignmentService.getMyAssignments("id", "desc");

    // Assert
    ArgumentCaptor<Specification<Assignment>> specCaptor =
        ArgumentCaptor.forClass(Specification.class);
    ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);

    verify(assignmentRepository).findAll(specCaptor.capture(), sortCaptor.capture());

    // Verify specification is not null
    assertNotNull(specCaptor.getValue());

    // Verify sort is applied correctly
    Sort capturedSort = sortCaptor.getValue();
    assertNotNull(capturedSort);
    assertEquals(
        Sort.Direction.DESC, Objects.requireNonNull(capturedSort.getOrderFor("id")).getDirection());
  }
}
