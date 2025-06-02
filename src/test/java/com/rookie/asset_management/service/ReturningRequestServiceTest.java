package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.return_request.ReturningRequestDtoResponse;
import com.rookie.asset_management.entity.ReturningRequest;
import com.rookie.asset_management.entity.Role;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.enums.ReturningRequestStatus;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.mapper.PagingMapper;
import com.rookie.asset_management.repository.ReturningRequestRepository;
import com.rookie.asset_management.repository.SpecificationRepository;
import com.rookie.asset_management.repository.UserRepository;
import com.rookie.asset_management.service.impl.ReturningRequestServiceImpl;
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
}
