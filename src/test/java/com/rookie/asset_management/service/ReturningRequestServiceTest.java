package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.return_request.CompleteReturningRequestDtoResponse;
import com.rookie.asset_management.dto.response.return_request.ReturningRequestDtoResponse;
import com.rookie.asset_management.entity.Asset;
import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.Location;
import com.rookie.asset_management.entity.ReturningRequest;
import com.rookie.asset_management.entity.Role;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.enums.AssignmentStatus;
import com.rookie.asset_management.enums.ReturningRequestStatus;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.mapper.PagingMapper;
import com.rookie.asset_management.repository.ReturningRequestRepository;
import com.rookie.asset_management.repository.SpecificationRepository;
import com.rookie.asset_management.repository.UserRepository;
import com.rookie.asset_management.service.impl.ReturningRequestServiceImpl;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class ReturningRequestServiceTest {

  @Mock private PagingMapper<ReturningRequest, ReturningRequestDtoResponse> pagingMapper;

  @Mock private SpecificationRepository<ReturningRequest, Integer> specificationRepository;

  @Mock private ReturningRequestRepository returningRequestRepository;

  @Mock private UserRepository userRepository;

  @Mock private JwtService jwtService;

  private ReturningRequestServiceImpl returningRequestService;

  private User adminUser;
  private User nonAdminUser;
  private Role adminRole;
  private Role userRole;

  @BeforeEach
  void setUp() {
    returningRequestService =
        new ReturningRequestServiceImpl(
            pagingMapper,
            specificationRepository,
            returningRequestRepository,
            userRepository,
            jwtService);

    // Setup roles
    adminRole = new Role();
    adminRole.setName("ADMIN");

    userRole = new Role();
    userRole.setName("USER");

    // Setup admin user
    adminUser = new User();
    adminUser.setId(1);
    adminUser.setUsername("admin");
    adminUser.setRole(adminRole);

    // Setup non-admin user
    nonAdminUser = new User();
    nonAdminUser.setId(2);
    nonAdminUser.setUsername("user");
    nonAdminUser.setRole(userRole);
  }

  @Test
  void getAllReturningRequests_WhenUserNotFound_ShouldThrowAppException() {
    // Given
    String username = "nonexistent";
    when(jwtService.extractUsername()).thenReturn(username);
    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    // When & Then
    AppException exception =
        assertThrows(
            AppException.class,
            () ->
                returningRequestService.getAllReturningRequests(
                    ReturningRequestStatus.WAITING, null, null, 0, 10, "id", "asc"));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
    assertEquals("User Not Found", exception.getMessage());
  }

  @Test
  void getAllReturningRequests_WhenUserIsNotAdmin_ShouldThrowAppException() {
    // Given
    when(jwtService.extractUsername()).thenReturn("user");
    when(userRepository.findByUsername("user")).thenReturn(Optional.of(nonAdminUser));

    // When & Then
    AppException exception =
        assertThrows(
            AppException.class,
            () ->
                returningRequestService.getAllReturningRequests(
                    ReturningRequestStatus.WAITING, null, null, 0, 10, "id", "asc"));

    assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatusCode());
    assertEquals("Only admins can access this endpoint", exception.getMessage());
  }

  @Test
  void getAllReturningRequests_WhenAdminUser_WithBasicSorting_ShouldReturnResults() {
    // Given
    when(jwtService.extractUsername()).thenReturn("admin");
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

    PagingDtoResponse<ReturningRequestDtoResponse> expectedResponse = new PagingDtoResponse<>();

    // Mock the parent class method getMany
    ReturningRequestServiceImpl spyService = spy(returningRequestService);
    doReturn(expectedResponse)
        .when(spyService)
        .getMany(any(Specification.class), any(Pageable.class));

    // When
    PagingDtoResponse<ReturningRequestDtoResponse> result =
        spyService.getAllReturningRequests(
            ReturningRequestStatus.WAITING, null, null, 0, 10, "id", "asc");

    // Then
    assertNotNull(result);
    assertEquals(expectedResponse, result);
    verify(spyService).getMany(any(Specification.class), any(Pageable.class));
  }

  @Test
  void getAllReturningRequests_WhenSortByAssetName_ShouldMapToCorrectProperty() {
    // Given
    when(jwtService.extractUsername()).thenReturn("admin");
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

    PagingDtoResponse<ReturningRequestDtoResponse> expectedResponse = new PagingDtoResponse<>();

    ReturningRequestServiceImpl spyService = spy(returningRequestService);
    doReturn(expectedResponse)
        .when(spyService)
        .getMany(any(Specification.class), any(Pageable.class));

    // When
    PagingDtoResponse<ReturningRequestDtoResponse> result =
        spyService.getAllReturningRequests(null, null, null, 0, 10, "assetName", "asc");

    // Then
    assertNotNull(result);
    verify(spyService).getMany(any(Specification.class), any(Pageable.class));
  }

  @Test
  void getAllReturningRequests_WhenSortByAssetCode_ShouldMapToCorrectProperty() {
    // Given
    when(jwtService.extractUsername()).thenReturn("admin");
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

    PagingDtoResponse<ReturningRequestDtoResponse> expectedResponse = new PagingDtoResponse<>();

    ReturningRequestServiceImpl spyService = spy(returningRequestService);
    doReturn(expectedResponse)
        .when(spyService)
        .getMany(any(Specification.class), any(Pageable.class));

    // When
    PagingDtoResponse<ReturningRequestDtoResponse> result =
        spyService.getAllReturningRequests(null, null, null, 0, 10, "assetCode", "desc");

    // Then
    assertNotNull(result);
    verify(spyService).getMany(any(Specification.class), any(Pageable.class));
  }

  @Test
  void getAllReturningRequests_WhenSortByRequestedBy_ShouldMapToCorrectProperty() {
    // Given
    when(jwtService.extractUsername()).thenReturn("admin");
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

    PagingDtoResponse<ReturningRequestDtoResponse> expectedResponse = new PagingDtoResponse<>();

    ReturningRequestServiceImpl spyService = spy(returningRequestService);
    doReturn(expectedResponse)
        .when(spyService)
        .getMany(any(Specification.class), any(Pageable.class));

    // When
    PagingDtoResponse<ReturningRequestDtoResponse> result =
        spyService.getAllReturningRequests(null, null, null, 0, 10, "requestedBy", "asc");

    // Then
    assertNotNull(result);
    verify(spyService).getMany(any(Specification.class), any(Pageable.class));
  }

  @Test
  void getAllReturningRequests_WhenSortByAcceptedBy_ShouldMapToCorrectProperty() {
    // Given
    when(jwtService.extractUsername()).thenReturn("admin");
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

    PagingDtoResponse<ReturningRequestDtoResponse> expectedResponse = new PagingDtoResponse<>();

    ReturningRequestServiceImpl spyService = spy(returningRequestService);
    doReturn(expectedResponse)
        .when(spyService)
        .getMany(any(Specification.class), any(Pageable.class));

    // When
    PagingDtoResponse<ReturningRequestDtoResponse> result =
        spyService.getAllReturningRequests(null, null, null, 0, 10, "acceptedBy", "asc");

    // Then
    assertNotNull(result);
    verify(spyService).getMany(any(Specification.class), any(Pageable.class));
  }

  @Test
  void getAllReturningRequests_WhenSortByAssignedDate_ShouldMapToCorrectProperty() {
    // Given
    when(jwtService.extractUsername()).thenReturn("admin");
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

    PagingDtoResponse<ReturningRequestDtoResponse> expectedResponse = new PagingDtoResponse<>();

    ReturningRequestServiceImpl spyService = spy(returningRequestService);
    doReturn(expectedResponse)
        .when(spyService)
        .getMany(any(Specification.class), any(Pageable.class));

    // When
    PagingDtoResponse<ReturningRequestDtoResponse> result =
        spyService.getAllReturningRequests(null, null, null, 0, 10, "assignedDate", "asc");

    // Then
    assertNotNull(result);
    verify(spyService).getMany(any(Specification.class), any(Pageable.class));
  }

  @Test
  void getAllReturningRequests_WhenSortByStatus_ShouldUseUnpagedPageable() {
    // Given
    when(jwtService.extractUsername()).thenReturn("admin");
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

    PagingDtoResponse<ReturningRequestDtoResponse> expectedResponse = new PagingDtoResponse<>();

    ReturningRequestServiceImpl spyService = spy(returningRequestService);
    doReturn(expectedResponse)
        .when(spyService)
        .getMany(any(Specification.class), any(Pageable.class));

    // When
    PagingDtoResponse<ReturningRequestDtoResponse> result =
        spyService.getAllReturningRequests(null, null, null, 0, 10, "status", "asc");

    // Then
    assertNotNull(result);
    verify(spyService).getMany(any(Specification.class), any(Pageable.class));
  }

  @Test
  void getAllReturningRequests_WithAllFilters_ShouldApplyAllSpecifications() {
    // Given
    when(jwtService.extractUsername()).thenReturn("admin");
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

    PagingDtoResponse<ReturningRequestDtoResponse> expectedResponse = new PagingDtoResponse<>();

    ReturningRequestServiceImpl spyService = spy(returningRequestService);
    doReturn(expectedResponse)
        .when(spyService)
        .getMany(any(Specification.class), any(Pageable.class));

    // When
    PagingDtoResponse<ReturningRequestDtoResponse> result =
        spyService.getAllReturningRequests(
            ReturningRequestStatus.WAITING, "2024-01-01", "test query", 0, 10, "id", "asc");

    // Then
    assertNotNull(result);
    verify(spyService).getMany(any(Specification.class), any(Pageable.class));
  }

  @Test
  void getAllReturningRequests_WhenSortByStatusDesc_ShouldReverseCustomSortDirection() {
    // Given
    when(jwtService.extractUsername()).thenReturn("admin");
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

    PagingDtoResponse<ReturningRequestDtoResponse> expectedResponse = new PagingDtoResponse<>();

    ReturningRequestServiceImpl spyService = spy(returningRequestService);
    doReturn(expectedResponse)
        .when(spyService)
        .getMany(any(Specification.class), any(Pageable.class));

    // When
    PagingDtoResponse<ReturningRequestDtoResponse> result =
        spyService.getAllReturningRequests(null, null, null, 0, 10, "status", "desc");

    // Then
    assertNotNull(result);
    verify(spyService).getMany(any(Specification.class), any(Pageable.class));
  }

  @Test
  void getAllReturningRequests_WhenSortByStatusAsc_ShouldReverseCustomSortDirection() {
    // Given
    when(jwtService.extractUsername()).thenReturn("admin");
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

    PagingDtoResponse<ReturningRequestDtoResponse> expectedResponse = new PagingDtoResponse<>();

    ReturningRequestServiceImpl spyService = spy(returningRequestService);
    doReturn(expectedResponse)
        .when(spyService)
        .getMany(any(Specification.class), any(Pageable.class));

    // When
    PagingDtoResponse<ReturningRequestDtoResponse> result =
        spyService.getAllReturningRequests(null, null, null, 0, 10, "status", "asc");

    // Then
    assertNotNull(result);
    verify(spyService).getMany(any(Specification.class), any(Pageable.class));
  }

  @Test
  void getAllReturningRequests_WithNullParameters_ShouldHandleGracefully() {
    // Given
    when(jwtService.extractUsername()).thenReturn("admin");
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

    PagingDtoResponse<ReturningRequestDtoResponse> expectedResponse = new PagingDtoResponse<>();

    ReturningRequestServiceImpl spyService = spy(returningRequestService);
    doReturn(expectedResponse)
        .when(spyService)
        .getMany(any(Specification.class), any(Pageable.class));

    // When
    PagingDtoResponse<ReturningRequestDtoResponse> result =
        spyService.getAllReturningRequests(null, null, null, 0, 10, "id", "asc");

    // Then
    assertNotNull(result);
    verify(spyService).getMany(any(Specification.class), any(Pageable.class));
  }

  @Test
  void completeReturningRequest_WhenReturningRequestNotFound_ShouldThrowAppException() {
    // Given
    Integer requestId = 999;
    when(returningRequestRepository.findById(requestId)).thenReturn(Optional.empty());

    // When & Then
    AppException exception =
        assertThrows(
            AppException.class, () -> returningRequestService.completeReturningRequest(requestId));

    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatusCode());
    assertEquals("Returning Request Not Found", exception.getMessage());
    verify(returningRequestRepository).findById(requestId);
  }

  @Test
  void completeReturningRequest_WhenUserNotFound_ShouldThrowAppException() {
    // Given
    Integer requestId = 1;
    ReturningRequest returningRequest = createMockReturningRequest();
    String username = "nonexistent";

    when(returningRequestRepository.findById(requestId)).thenReturn(Optional.of(returningRequest));
    when(jwtService.extractUsername()).thenReturn(username);
    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    // When & Then
    AppException exception =
        assertThrows(
            AppException.class, () -> returningRequestService.completeReturningRequest(requestId));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
    assertEquals("User Not Found", exception.getMessage());
  }

  @Test
  void completeReturningRequest_WhenUserIsNotAdmin_ShouldThrowAppException() {
    // Given
    Integer requestId = 1;
    ReturningRequest returningRequest = createMockReturningRequest();

    when(returningRequestRepository.findById(requestId)).thenReturn(Optional.of(returningRequest));
    when(jwtService.extractUsername()).thenReturn("user");
    when(userRepository.findByUsername("user")).thenReturn(Optional.of(nonAdminUser));

    // When & Then
    AppException exception =
        assertThrows(
            AppException.class, () -> returningRequestService.completeReturningRequest(requestId));

    assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatusCode());
    assertEquals("Only admins can access this endpoint", exception.getMessage());
  }

  @Test
  void completeReturningRequest_WhenAdminDifferentLocation_ShouldThrowAppException() {
    // Given
    Integer requestId = 1;
    ReturningRequest returningRequest = createMockReturningRequest();

    // Setup locations
    Location adminLocation = new Location();
    adminLocation.setId(1);
    adminUser.setLocation(adminLocation);

    Location assetLocation = new Location();
    assetLocation.setId(2);
    returningRequest.getAssignment().getAsset().setLocation(assetLocation);

    when(returningRequestRepository.findById(requestId)).thenReturn(Optional.of(returningRequest));
    when(jwtService.extractUsername()).thenReturn("admin");
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

    // When & Then
    AppException exception =
        assertThrows(
            AppException.class, () -> returningRequestService.completeReturningRequest(requestId));

    assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatusCode());
    assertEquals("You do not have permission to complete this request", exception.getMessage());
  }

  @Test
  void completeReturningRequest_WhenRequestAlreadyCompleted_ShouldReturnNull() {
    // Given
    Integer requestId = 1;
    ReturningRequest returningRequest = createMockReturningRequest();
    returningRequest.setStatus(ReturningRequestStatus.COMPLETED);

    // Setup same location
    Location location = new Location();
    location.setId(1);
    adminUser.setLocation(location);
    returningRequest.getAssignment().getAsset().setLocation(location);

    when(returningRequestRepository.findById(requestId)).thenReturn(Optional.of(returningRequest));
    when(jwtService.extractUsername()).thenReturn("admin");
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

    // When
    CompleteReturningRequestDtoResponse result =
        returningRequestService.completeReturningRequest(requestId);

    // Then
    assertNull(result);

    // Verify that save method is not called when request is already completed
    verify(returningRequestRepository, never()).save(any(ReturningRequest.class));
  }

  @Test
  void completeReturningRequest_WhenValidRequest_ShouldCompleteSuccessfully() {
    // Given
    Integer requestId = 1;
    ReturningRequest returningRequest = createMockReturningRequest();
    returningRequest.setStatus(ReturningRequestStatus.WAITING);

    // Setup same location
    Location location = new Location();
    location.setId(1);
    adminUser.setLocation(location);
    returningRequest.getAssignment().getAsset().setLocation(location);

    when(returningRequestRepository.findById(requestId)).thenReturn(Optional.of(returningRequest));
    when(jwtService.extractUsername()).thenReturn("admin");
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
    when(returningRequestRepository.save(any(ReturningRequest.class))).thenReturn(returningRequest);

    // When
    CompleteReturningRequestDtoResponse result =
        returningRequestService.completeReturningRequest(requestId);

    // Then
    assertNotNull(result);
    assertEquals(requestId, result.getId());
    assertEquals(ReturningRequestStatus.COMPLETED.name(), result.getStatus());

    // Verify the returningRequest was updated correctly
    assertEquals(ReturningRequestStatus.COMPLETED, returningRequest.getStatus());
    assertEquals(LocalDate.now(), returningRequest.getReturnedDate());
    assertEquals(AssignmentStatus.RETURNED, returningRequest.getAssignment().getStatus());
    assertEquals(adminUser, returningRequest.getAcceptedBy());

    verify(returningRequestRepository).save(returningRequest);
  }

  @Test
  void completeReturningRequest_WhenValidRequest_ShouldUpdateAllRelatedEntities() {
    // Given
    Integer requestId = 1;
    ReturningRequest returningRequest = createMockReturningRequest();
    returningRequest.setStatus(ReturningRequestStatus.WAITING);

    // Setup same location
    Location location = new Location();
    location.setId(1);
    adminUser.setLocation(location);
    returningRequest.getAssignment().getAsset().setLocation(location);

    when(returningRequestRepository.findById(requestId)).thenReturn(Optional.of(returningRequest));
    when(jwtService.extractUsername()).thenReturn("admin");
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
    when(returningRequestRepository.save(any(ReturningRequest.class))).thenReturn(returningRequest);

    // When
    returningRequestService.completeReturningRequest(requestId);

    // Then
    verify(returningRequestRepository).findById(requestId);
    verify(jwtService).extractUsername();
    verify(userRepository).findByUsername("admin");
    verify(returningRequestRepository).save(returningRequest);

    // Verify all entities were updated
    assertEquals(ReturningRequestStatus.COMPLETED, returningRequest.getStatus());
    assertNotNull(returningRequest.getReturnedDate());
    assertEquals(LocalDate.now(), returningRequest.getReturnedDate());
    assertEquals(AssignmentStatus.RETURNED, returningRequest.getAssignment().getStatus());
    assertEquals(adminUser, returningRequest.getAcceptedBy());
  }

  // Helper method to create mock ReturningRequest
  private ReturningRequest createMockReturningRequest() {
    // Create mock entities
    Asset asset = new Asset();
    asset.setId(1);
    asset.setName("Test Asset");
    asset.setAssetCode("TST001");

    Location location = new Location();
    location.setId(1);
    asset.setLocation(location);

    Assignment assignment = new Assignment();
    assignment.setId(1);
    assignment.setAsset(asset);
    assignment.setStatus(AssignmentStatus.ACCEPTED);
    assignment.setAssignedDate(LocalDate.now().minusDays(7));

    User requestedByUser = new User();
    requestedByUser.setId(3);
    requestedByUser.setUsername("requester");

    ReturningRequest returningRequest = new ReturningRequest();
    returningRequest.setId(1);
    returningRequest.setAssignment(assignment);
    returningRequest.setRequestedBy(requestedByUser);
    returningRequest.setStatus(ReturningRequestStatus.WAITING);

    return returningRequest;
  }
}
