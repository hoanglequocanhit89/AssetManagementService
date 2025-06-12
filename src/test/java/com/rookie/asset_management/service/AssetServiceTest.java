package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.rookie.asset_management.dto.request.asset.CreateNewAssetDtoRequest;
import com.rookie.asset_management.dto.request.asset.EditAssetDtoRequest;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.asset.AssetDetailDtoResponse;
import com.rookie.asset_management.dto.response.asset.CreateNewAssetDtoResponse;
import com.rookie.asset_management.dto.response.asset.EditAssetDtoResponse;
import com.rookie.asset_management.dto.response.asset.ViewAssetListDtoResponse;
import com.rookie.asset_management.dto.response.assignment.AssignmentDtoResponse;
import com.rookie.asset_management.entity.Asset;
import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.Category;
import com.rookie.asset_management.entity.Location;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.entity.UserDetailModel;
import com.rookie.asset_management.enums.AssetStatus;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.mapper.AssetMapper;
import com.rookie.asset_management.repository.AssetRepository;
import com.rookie.asset_management.service.impl.AssetServiceImpl;
import com.rookie.asset_management.util.SecurityUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

  @Mock private AssetRepository assetRepository;
  @Mock private AssetMapper assetMapper;
  @InjectMocks private AssetServiceImpl assetService;

  private Asset asset;
  private Location location;

  @BeforeEach
  void setUp() {
    Category category = new Category();
    category.setId(1);
    category.setName("Laptop");

    location = new Location();
    location.setId(1);
    location.setName("HCM");

    asset = new Asset();
    asset.setId(1);
    asset.setAssetCode("LA0001");
    asset.setName("Laptop Dell");
    asset.setSpecification("Core i7, 16GB RAM");
    asset.setInstalledDate(LocalDate.of(2023, 1, 1));
    asset.setCategory(category);
    asset.setLocation(location);
    asset.setStatus(AssetStatus.AVAILABLE);
    asset.setDisabled(false);
  }

  @BeforeEach
  void setupSecurityContext() {
    // Clear any existing authentication
    SecurityContextHolder.clearContext();
  }

  private void mockAuthenticatedUser(User user) {
    UserDetailModel userDetails = new UserDetailModel(user); // hoặc mock(UserDetailModel.class)
    try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
      // Create authentication with the user
      UsernamePasswordAuthenticationToken auth =
          new UsernamePasswordAuthenticationToken(userDetails, "password", Collections.emptyList());

      // Add user principal to the authentication
      auth.setDetails(user);

      // Set the authentication in the security context
      SecurityContextHolder.getContext().setAuthentication(auth);

      mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(user);
    }
  }

  @Test
  void searchFilterAndSortAssets_ShouldReturnPagedAssets() {
    // Arrange
    Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
    List<Asset> assets = List.of(asset);
    Page<Asset> pageAssets = new PageImpl<>(assets, pageable, 1);

    when(assetRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageAssets);

    // Create expected response
    ViewAssetListDtoResponse responseDto = new ViewAssetListDtoResponse();
    responseDto.setAssetCode("LA0001");
    responseDto.setName("Laptop Dell");
    responseDto.setCategoryName("Laptop");
    responseDto.setStatus(AssetStatus.AVAILABLE);
    responseDto.setLocationName("HCM");

    PagingDtoResponse<ViewAssetListDtoResponse> expectedResponse = new PagingDtoResponse<>();
    expectedResponse.setContent(List.of(responseDto));

    when(assetMapper.toPagingResult(any(Page.class), any())).thenReturn(expectedResponse);

    User user = new User();
    user.setId(1);
    user.setUsername("admin1");
    user.setLocation(location);
    user.setDisabled(false);
    mockAuthenticatedUser(user);

    // Act
    PagingDtoResponse<ViewAssetListDtoResponse> result =
        assetService.getAllAssets("laptop", "Laptop", List.of(AssetStatus.AVAILABLE), pageable);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getContent().size());

    List<ViewAssetListDtoResponse> content = new ArrayList<>(result.getContent());
    ViewAssetListDtoResponse response = content.get(0);

    assertEquals("LA0001", response.getAssetCode());
    assertEquals("Laptop Dell", response.getName());
    assertEquals("Laptop", response.getCategoryName());
    assertEquals(AssetStatus.AVAILABLE, response.getStatus());
    assertEquals("HCM", response.getLocationName());

    verify(assetRepository, times(1)).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void searchAssets_WithOnlyLocationId_ShouldReturnPagedAssets() {
    // Arrange
    Pageable pageable = PageRequest.of(0, 10);
    List<Asset> assets = List.of(asset);
    Page<Asset> pageAssets = new PageImpl<>(assets, pageable, 1);

    when(assetRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageAssets);

    // Create expected response
    ViewAssetListDtoResponse responseDto = new ViewAssetListDtoResponse();
    responseDto.setAssetCode("LA0001");
    responseDto.setName("Laptop Dell");
    responseDto.setCategoryName("Laptop");
    responseDto.setStatus(AssetStatus.AVAILABLE);
    responseDto.setLocationName("HCM");

    PagingDtoResponse<ViewAssetListDtoResponse> expectedResponse = new PagingDtoResponse<>();
    expectedResponse.setContent(List.of(responseDto));

    when(assetMapper.toPagingResult(any(Page.class), any())).thenReturn(expectedResponse);

    User user = new User();
    user.setId(1);
    user.setUsername("admin1");
    user.setLocation(location);
    user.setDisabled(false);
    mockAuthenticatedUser(user);

    // Act
    PagingDtoResponse<ViewAssetListDtoResponse> result =
        assetService.getAllAssets(null, null, null, pageable);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    verify(assetRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void searchAssets_WithKeywordOnly_ShouldReturnPagedAssets() {
    // Arrange
    Pageable pageable = PageRequest.of(0, 10);
    List<Asset> assets = List.of(asset);
    Page<Asset> pageAssets = new PageImpl<>(assets, pageable, 1);

    when(assetRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageAssets);

    // Create expected response
    ViewAssetListDtoResponse responseDto = new ViewAssetListDtoResponse();
    responseDto.setAssetCode("LA0001");
    responseDto.setName("Laptop Dell");
    responseDto.setCategoryName("Laptop");
    responseDto.setStatus(AssetStatus.AVAILABLE);
    responseDto.setLocationName("HCM");

    PagingDtoResponse<ViewAssetListDtoResponse> expectedResponse = new PagingDtoResponse<>();
    expectedResponse.setContent(List.of(responseDto));

    when(assetMapper.toPagingResult(any(Page.class), any())).thenReturn(expectedResponse);

    User user = new User();
    user.setId(1);
    user.setUsername("admin1");
    user.setLocation(location);
    user.setDisabled(false);
    mockAuthenticatedUser(user);

    // Act
    PagingDtoResponse<ViewAssetListDtoResponse> result =
        assetService.getAllAssets("laptop", null, null, pageable);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    verify(assetRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void searchAssets_WithCategoryOnly_ShouldReturnPagedAssets() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Asset> pageAssets = new PageImpl<>(List.of(asset), pageable, 1);

    when(assetRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageAssets);

    // Create expected response
    ViewAssetListDtoResponse responseDto = new ViewAssetListDtoResponse();
    responseDto.setAssetCode("LA0001");
    responseDto.setName("Laptop Dell");
    responseDto.setCategoryName("Laptop");
    responseDto.setStatus(AssetStatus.AVAILABLE);
    responseDto.setLocationName("HCM");

    PagingDtoResponse<ViewAssetListDtoResponse> expectedResponse = new PagingDtoResponse<>();
    expectedResponse.setContent(List.of(responseDto));

    when(assetMapper.toPagingResult(any(Page.class), any())).thenReturn(expectedResponse);

    User user = new User();
    user.setId(1);
    user.setUsername("admin1");
    user.setLocation(location);
    user.setDisabled(false);
    mockAuthenticatedUser(user);

    PagingDtoResponse<ViewAssetListDtoResponse> result =
        assetService.getAllAssets(null, "Laptop", null, pageable);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    verify(assetRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void searchAssets_WithStatesOnly_ShouldReturnPagedAssets() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Asset> pageAssets = new PageImpl<>(List.of(asset), pageable, 1);

    when(assetRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageAssets);

    // Create expected response
    ViewAssetListDtoResponse responseDto = new ViewAssetListDtoResponse();
    responseDto.setAssetCode("LA0001");
    responseDto.setName("Laptop Dell");
    responseDto.setCategoryName("Laptop");
    responseDto.setStatus(AssetStatus.AVAILABLE);
    responseDto.setLocationName("HCM");

    PagingDtoResponse<ViewAssetListDtoResponse> expectedResponse = new PagingDtoResponse<>();
    expectedResponse.setContent(List.of(responseDto));

    when(assetMapper.toPagingResult(any(Page.class), any())).thenReturn(expectedResponse);

    // Setup authenticated user
    User user = new User();
    user.setId(1);
    user.setUsername("admin1");
    user.setLocation(location);
    user.setDisabled(false);
    mockAuthenticatedUser(user);

    PagingDtoResponse<ViewAssetListDtoResponse> result =
        assetService.getAllAssets(null, null, List.of(AssetStatus.AVAILABLE), pageable);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    verify(assetRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void searchAssets_WithBlankFilters_ShouldReturnPagedAssets() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Asset> pageAssets = new PageImpl<>(List.of(asset), pageable, 1);

    when(assetRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageAssets);

    // Create expected response
    ViewAssetListDtoResponse responseDto = new ViewAssetListDtoResponse();
    responseDto.setAssetCode("LA0001");
    responseDto.setName("Laptop Dell");
    responseDto.setCategoryName("Laptop");
    responseDto.setStatus(AssetStatus.AVAILABLE);
    responseDto.setLocationName("HCM");

    PagingDtoResponse<ViewAssetListDtoResponse> expectedResponse = new PagingDtoResponse<>();
    expectedResponse.setContent(List.of(responseDto));

    when(assetMapper.toPagingResult(any(Page.class), any())).thenReturn(expectedResponse);

    // Setup authenticated user
    User user = new User();
    user.setId(1);
    user.setUsername("admin1");
    user.setLocation(location);
    user.setDisabled(false);
    mockAuthenticatedUser(user);

    PagingDtoResponse<ViewAssetListDtoResponse> result =
        assetService.getAllAssets("   ", "", List.of(), pageable);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    verify(assetRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void createNewAsset_Success() {
    // Arrange
    CreateNewAssetDtoRequest request =
        CreateNewAssetDtoRequest.builder()
            .name("Laptop Dell")
            .specification("i7, 16GB RAM")
            .installedDate(LocalDate.now())
            .state(AssetStatus.AVAILABLE)
            .categoryId(1)
            .build();

    Category category = new Category();
    category.setId(1);
    category.setName("Laptop");
    category.setPrefix("LA");

    Location location = new Location();
    location.setId(1);
    location.setName("HCM");

    User admin = new User();
    admin.setId(1);
    admin.setUsername("admin1");
    admin.setLocation(location);
    admin.setDisabled(false);

    Asset savedAsset = new Asset();
    savedAsset.setId(123); // mock ID after save
    savedAsset.setName(request.getName());
    savedAsset.setSpecification(request.getSpecification());
    savedAsset.setInstalledDate(request.getInstalledDate());
    savedAsset.setCategory(category);
    savedAsset.setLocation(location);
    savedAsset.setStatus(request.getState());
    savedAsset.setAssetCode("LA000123");
    savedAsset.setCreatedAt(new Date());
    savedAsset.setDisabled(false);

    // Add mock for the DTO to entity conversion
    Asset mappedAsset = new Asset();
    mappedAsset.setName(request.getName());
    mappedAsset.setSpecification(request.getSpecification());
    mappedAsset.setInstalledDate(request.getInstalledDate());
    mappedAsset.setStatus(request.getState());
    mappedAsset.setCategory(category);
    when(assetMapper.toEntity(request)).thenReturn(mappedAsset);
    mockAuthenticatedUser(admin);
    when(assetRepository.findByNameAndLocation("Laptop Dell", location))
        .thenReturn(Collections.emptyList());
    when(assetRepository.save(any(Asset.class)))
        .thenAnswer(
            invocation -> {
              Asset a = invocation.getArgument(0);
              if (a.getId() == null) {
                a.setId(123); // Set ID for first save (when assetCode is "PENDING")
              }
              return a;
            });

    when(assetMapper.toCreationDto(any(Asset.class)))
        .thenReturn(
            CreateNewAssetDtoResponse.builder()
                .id(123)
                .assetCode("LA000123")
                .name("Laptop Dell")
                .specification("i7, 16GB RAM")
                .installedDate(request.getInstalledDate())
                .state(AssetStatus.AVAILABLE)
                .categoryName("Laptop")
                .locationName("HCM")
                .build());

    // Act
    CreateNewAssetDtoResponse response = assetService.createNewAsset(request);

    // Assert
    assertNotNull(response);
    assertEquals("LA000123", response.getAssetCode());
    assertEquals("Laptop Dell", response.getName());
    assertEquals("i7, 16GB RAM", response.getSpecification());
    assertEquals(request.getInstalledDate(), response.getInstalledDate());
    assertEquals(AssetStatus.AVAILABLE, response.getState());
    assertEquals("Laptop", response.getCategoryName());
    assertEquals("HCM", response.getLocationName());
    assertEquals(123, response.getId());

    // Verify interactions
    verify(assetRepository, times(1)).findByNameAndLocation("Laptop Dell", location);
    verify(assetRepository, times(1))
        .save(
            any(Asset.class)); // Called twice: first for ID generation, second for assetCode update
    verify(assetMapper, times(1)).toCreationDto(any(Asset.class));
  }

  @Test
  void createNewAsset_DuplicateNameInLocation_ThrowsException() {
    // Arrange
    CreateNewAssetDtoRequest request =
        CreateNewAssetDtoRequest.builder()
            .name("Laptop Dell")
            .specification("i7, 16GB RAM")
            .installedDate(LocalDate.now())
            .state(AssetStatus.AVAILABLE)
            .categoryId(1)
            .build();

    Category category = new Category();
    category.setId(1);
    category.setName("Laptop");
    category.setPrefix("LA");

    Location location = new Location();
    location.setId(1);
    location.setName("HCM");

    User admin = new User();
    admin.setId(1);
    admin.setUsername("admin1");
    admin.setLocation(location);
    admin.setDisabled(false); // Set disabled status to prevent NPE

    mockAuthenticatedUser(admin);
    Asset existingAsset = new Asset();
    existingAsset.setDisabled(false); // simulate an active (not deleted) asset
    when(assetRepository.findByNameAndLocation("Laptop Dell", location))
        .thenReturn(List.of(existingAsset));

    // Act & Assert
    AppException ex = assertThrows(AppException.class, () -> assetService.createNewAsset(request));

    assertEquals(HttpStatus.CONFLICT, ex.getHttpStatusCode());
    assertTrue(ex.getMessage().contains("Asset name already exists in this location"));

    // Verify all interactions up to duplicate check
    verify(assetRepository, times(1)).findByNameAndLocation("Laptop Dell", location);
    verify(assetRepository, never()).save(any());
  }

  @Test
  void createNewAsset_WithNotAvailableState_Success() {
    // Arrange
    CreateNewAssetDtoRequest request =
        CreateNewAssetDtoRequest.builder()
            .name("Laptop Dell")
            .specification("i7, 16GB RAM")
            .installedDate(LocalDate.now())
            .state(AssetStatus.NOT_AVAILABLE) // Test NOT_AVAILABLE state
            .categoryId(1)
            .build();

    Category category = new Category();
    category.setId(1);
    category.setName("Laptop");
    category.setPrefix("LA");

    Location location = new Location();
    location.setId(1);
    location.setName("HCM");

    User admin = new User();
    admin.setId(1);
    admin.setUsername("admin1");
    admin.setLocation(location);
    admin.setDisabled(false);

    mockAuthenticatedUser(admin);
    when(assetRepository.findByNameAndLocation("Laptop Dell", location))
        .thenReturn(Collections.emptyList());

    // Add mock for assetMapper.toEntity
    Asset mappedAsset = new Asset();
    mappedAsset.setName(request.getName());
    mappedAsset.setSpecification(request.getSpecification());
    mappedAsset.setInstalledDate(request.getInstalledDate());
    mappedAsset.setStatus(request.getState());
    mappedAsset.setCategory(category);
    when(assetMapper.toEntity(request)).thenReturn(mappedAsset);

    when(assetRepository.save(any(Asset.class)))
        .thenAnswer(
            invocation -> {
              Asset a = invocation.getArgument(0);
              if (a.getId() == null) {
                a.setId(123);
              }
              return a;
            });

    when(assetMapper.toCreationDto(any(Asset.class)))
        .thenReturn(
            CreateNewAssetDtoResponse.builder()
                .id(123)
                .assetCode("LA000123")
                .name("Laptop Dell")
                .specification("i7, 16GB RAM")
                .installedDate(request.getInstalledDate())
                .state(AssetStatus.NOT_AVAILABLE)
                .categoryName("Laptop")
                .locationName("HCM")
                .build());

    // Act
    CreateNewAssetDtoResponse response = assetService.createNewAsset(request);

    // Assert
    assertNotNull(response);
    assertEquals(AssetStatus.NOT_AVAILABLE, response.getState());
    assertEquals("LA000123", response.getAssetCode());
    assertEquals("Laptop Dell", response.getName());

    verify(assetRepository, times(1)).save(any(Asset.class));
  }

  @Test
  void editAsset_Success() {
    // Arrange
    Integer assetId = 1;

    EditAssetDtoRequest request =
        EditAssetDtoRequest.builder()
            .name("Updated Laptop")
            .specification("Updated Specs")
            .installedDate(LocalDate.now())
            .state(AssetStatus.NOT_AVAILABLE)
            .build();

    Location location = new Location();
    location.setId(1);
    location.setName("HCM");

    Category category = new Category();
    category.setId(1);
    category.setName("Laptop");

    User admin = new User();
    admin.setId(2);
    admin.setUsername("admin1");
    admin.setLocation(location);
    admin.setDisabled(false);

    Asset asset = new Asset();
    asset.setId(assetId);
    asset.setAssetCode("LA0001");
    asset.setName("Old Laptop");
    asset.setSpecification("Old Specs");
    asset.setInstalledDate(LocalDate.now().minusDays(1));
    asset.setStatus(AssetStatus.AVAILABLE);
    asset.setCategory(category);
    asset.setLocation(location);
    asset.setDisabled(false);

    when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
    mockAuthenticatedUser(admin);
    when(assetRepository.findByNameAndLocationAndIdNot("Updated Laptop", location, assetId))
        .thenReturn(Collections.emptyList());
    when(assetRepository.save(any(Asset.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    when(assetMapper.toEditionDto(any(Asset.class)))
        .thenReturn(
            EditAssetDtoResponse.builder()
                .id(assetId)
                .assetCode("LA0001")
                .name("Updated Laptop")
                .specification("Updated Specs")
                .installedDate(request.getInstalledDate())
                .state(AssetStatus.NOT_AVAILABLE)
                .categoryName("Laptop")
                .locationName("HCM")
                .updatedAt(new Date())
                .build());

    // Act
    EditAssetDtoResponse response = assetService.editAsset(assetId, request);

    // Assert
    assertNotNull(response);
    assertEquals(assetId, response.getId());
    assertEquals("LA0001", response.getAssetCode());
    assertEquals("Updated Laptop", response.getName());
    assertEquals("Updated Specs", response.getSpecification());
    assertEquals(request.getInstalledDate(), response.getInstalledDate());
    assertEquals(AssetStatus.NOT_AVAILABLE, response.getState());
    assertEquals("Laptop", response.getCategoryName());
    assertEquals("HCM", response.getLocationName());
    assertNotNull(response.getUpdatedAt());

    // Verify interactions
    verify(assetRepository, times(1)).findById(assetId);
    verify(assetRepository, times(1))
        .findByNameAndLocationAndIdNot("Updated Laptop", location, assetId);
    verify(assetRepository, times(1)).save(asset);
  }

  @Test
  void editAsset_AssetNotFound_ThrowsException() {
    // Arrange
    Integer assetId = 999;
    EditAssetDtoRequest request =
        EditAssetDtoRequest.builder()
            .name("Test Asset")
            .specification("Test Spec")
            .installedDate(LocalDate.now())
            .state(AssetStatus.AVAILABLE)
            .build();

    when(assetRepository.findById(assetId)).thenReturn(Optional.empty());

    // Act & Assert
    AppException ex =
        assertThrows(AppException.class, () -> assetService.editAsset(assetId, request));

    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatusCode());
    assertEquals("Asset not found", ex.getMessage());

    // Verify no further interactions
    verify(assetRepository, times(1)).findById(assetId);
    verify(assetRepository, never()).save(any());
  }

  @Test
  void editAsset_AssetAssigned_ThrowsException() {
    // Arrange
    Integer assetId = 1;
    Asset asset = new Asset();
    asset.setId(assetId);
    asset.setStatus(AssetStatus.ASSIGNED);

    EditAssetDtoRequest request =
        EditAssetDtoRequest.builder()
            .name("Test Asset")
            .specification("Test Spec")
            .installedDate(LocalDate.now())
            .state(AssetStatus.AVAILABLE)
            .build();

    when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));

    // Act & Assert
    AppException ex =
        assertThrows(AppException.class, () -> assetService.editAsset(assetId, request));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatusCode());
    assertEquals("Cannot edit assigned asset", ex.getMessage());

    // Verify no further interactions
    verify(assetRepository, times(1)).findById(assetId);
    verify(assetRepository, never()).save(any());
  }

  @Test
  void editAsset_UserNotFound_ThrowsException() {
    // Arrange
    Integer assetId = 1;
    Asset asset = new Asset();
    asset.setId(assetId);
    asset.setStatus(AssetStatus.AVAILABLE);

    EditAssetDtoRequest request =
        EditAssetDtoRequest.builder()
            .name("Test Asset")
            .specification("Test Spec")
            .installedDate(LocalDate.now())
            .state(AssetStatus.AVAILABLE)
            .build();

    when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));

    // Mock SecurityUtils to throw exception when getCurrentUser is called
    try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils
          .when(SecurityUtils::getCurrentUser)
          .thenThrow(new AppException(HttpStatus.BAD_REQUEST, "User Not Found"));

      // Act & Assert
      AppException ex =
          assertThrows(AppException.class, () -> assetService.editAsset(assetId, request));

      assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatusCode());
      assertEquals("User Not Found", ex.getMessage());
    }

    // Verify interactions
    verify(assetRepository, times(1)).findById(assetId);
    verify(assetRepository, never()).findByNameAndLocationAndIdNot(any(), any(), any());
    verify(assetRepository, never()).save(any());
  }

  @Test
  void editAsset_NameAlreadyExistsInLocation_ThrowsException() {
    // Arrange
    Integer assetId = 1;

    Location location = new Location();
    location.setId(1);
    location.setName("HCM");

    Category category = new Category();
    category.setId(1);
    category.setName("Laptop");

    User admin = new User();
    admin.setId(2);
    admin.setUsername("admin1");
    admin.setLocation(location);
    admin.setDisabled(false);

    Asset asset = new Asset();
    asset.setId(assetId);
    asset.setName("Old Asset Name");
    asset.setStatus(AssetStatus.AVAILABLE);
    asset.setLocation(location);
    asset.setCategory(category);
    asset.setDisabled(false);

    Asset existingAsset = new Asset();
    existingAsset.setId(2); // Different ID
    existingAsset.setName("Existing Asset Name");
    existingAsset.setLocation(location);
    existingAsset.setDisabled(false);
    List<Asset> mockAsset = List.of(existingAsset);

    EditAssetDtoRequest request =
        EditAssetDtoRequest.builder()
            .name("Existing Asset Name") // Different from current name
            .specification("Test Spec")
            .installedDate(LocalDate.now())
            .state(AssetStatus.NOT_AVAILABLE)
            .build();

    when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
    mockAuthenticatedUser(admin);
    when(assetRepository.findByNameAndLocationAndIdNot("Existing Asset Name", location, assetId))
        .thenReturn(mockAsset);

    // Act & Assert
    AppException ex =
        assertThrows(AppException.class, () -> assetService.editAsset(assetId, request));

    assertEquals(HttpStatus.CONFLICT, ex.getHttpStatusCode());
    assertEquals(
        "Asset name already exists in this location and is active. Please choose a different name.",
        ex.getMessage());

    // Verify all interactions up to duplicate check
    verify(assetRepository, times(1)).findById(assetId);
    verify(assetRepository, times(1))
        .findByNameAndLocationAndIdNot("Existing Asset Name", location, assetId);
    verify(assetRepository, never()).save(any());
  }

  @Test
  void editAsset_WithWaitingState_Success() {
    // Arrange
    Integer assetId = 1;

    Location location = new Location();
    location.setId(1);
    location.setName("HCM");

    Category category = new Category();
    category.setId(1);
    category.setName("Laptop");

    User admin = new User();
    admin.setId(2);
    admin.setUsername("admin1");
    admin.setLocation(location);
    admin.setDisabled(false);

    Asset asset = new Asset();
    asset.setId(assetId);
    asset.setAssetCode("LA0001");
    asset.setName("Test Asset");
    asset.setSpecification("Test Spec");
    asset.setInstalledDate(LocalDate.now().minusDays(1));
    asset.setStatus(AssetStatus.AVAILABLE);
    asset.setLocation(location);
    asset.setCategory(category);

    EditAssetDtoRequest request =
        EditAssetDtoRequest.builder()
            .name("Test Asset")
            .specification("Test Spec")
            .installedDate(LocalDate.now())
            .state(AssetStatus.WAITING) // Test WAITING state
            .build();

    when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
    mockAuthenticatedUser(admin);
    when(assetRepository.save(any(Asset.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    when(assetMapper.toEditionDto(any(Asset.class)))
        .thenReturn(
            EditAssetDtoResponse.builder()
                .id(assetId)
                .assetCode("LA0001")
                .name("Test Asset")
                .specification("Test Spec")
                .installedDate(request.getInstalledDate())
                .state(AssetStatus.WAITING)
                .categoryName("Laptop")
                .locationName("HCM")
                .updatedAt(new Date())
                .build());

    // Act
    EditAssetDtoResponse response = assetService.editAsset(assetId, request);

    // Assert
    assertNotNull(response);
    assertEquals(AssetStatus.WAITING, response.getState());

    verify(assetRepository, times(1)).findById(assetId);
    verify(assetRepository, times(1)).save(asset);
  }

  @Test
  void editAsset_WithRecycledState_Success() {
    // Arrange
    Integer assetId = 1;

    Location location = new Location();
    location.setId(1);
    location.setName("HCM");

    Category category = new Category();
    category.setId(1);
    category.setName("Laptop");

    User admin = new User();
    admin.setId(2);
    admin.setUsername("admin1");
    admin.setLocation(location);
    admin.setDisabled(false);

    Asset asset = new Asset();
    asset.setId(assetId);
    asset.setAssetCode("LA0001");
    asset.setName("Test Asset");
    asset.setSpecification("Test Spec");
    asset.setInstalledDate(LocalDate.now().minusDays(1));
    asset.setStatus(AssetStatus.AVAILABLE);
    asset.setLocation(location);
    asset.setCategory(category);

    EditAssetDtoRequest request =
        EditAssetDtoRequest.builder()
            .name("Test Asset")
            .specification("Test Spec")
            .installedDate(LocalDate.now())
            .state(AssetStatus.RECYCLED) // Test RECYCLED state
            .build();

    when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
    mockAuthenticatedUser(admin);
    when(assetRepository.save(any(Asset.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    when(assetMapper.toEditionDto(any(Asset.class)))
        .thenReturn(
            EditAssetDtoResponse.builder()
                .id(assetId)
                .assetCode("LA0001")
                .name("Test Asset")
                .specification("Test Spec")
                .installedDate(request.getInstalledDate())
                .state(AssetStatus.RECYCLED)
                .categoryName("Laptop")
                .locationName("HCM")
                .updatedAt(new Date())
                .build());

    // Act
    EditAssetDtoResponse response = assetService.editAsset(assetId, request);

    // Assert
    assertNotNull(response);
    assertEquals(AssetStatus.RECYCLED, response.getState());

    verify(assetRepository, times(1)).findById(assetId);
    verify(assetRepository, times(1)).save(asset);
  }

  @Test
  @DisplayName("should return asset detail when asset exists")
  void getAssetDetail_AssetExists_ReturnsAssetDetail() {
    // Given
    Integer assetId = 1;

    AssetDetailDtoResponse expectedAssetDetail = new AssetDetailDtoResponse();
    expectedAssetDetail.setId(assetId);
    expectedAssetDetail.setName("Test Asset");
    expectedAssetDetail.setSpecification("Test Specification");
    expectedAssetDetail.setInstalledDate(LocalDate.now());
    expectedAssetDetail.setLocation("Test Location");
    expectedAssetDetail.setAssignments(List.of());

    Asset mockedAsset = new Asset();
    mockedAsset.setId(assetId);
    mockedAsset.setName("Test Asset");
    mockedAsset.setSpecification("Test Specification");
    mockedAsset.setInstalledDate(LocalDate.now());

    Location location = new Location();
    location.setName("Test Location");
    mockedAsset.setLocation(location);

    Category category = new Category();
    category.setName("Test Category");
    mockedAsset.setCategory(category);

    mockedAsset.setAssignments(List.of());

    when(assetRepository.findById(assetId)).thenReturn(Optional.of(mockedAsset));
    when(assetMapper.toDetailDto(mockedAsset)).thenReturn(expectedAssetDetail);

    // When
    AssetDetailDtoResponse result = assetService.getAssetDetail(assetId);

    // Then
    assertEquals(expectedAssetDetail.getId(), result.getId());
    assertEquals(expectedAssetDetail.getName(), result.getName());
    assertEquals(expectedAssetDetail.getSpecification(), result.getSpecification());
    assertEquals(expectedAssetDetail.getInstalledDate(), result.getInstalledDate());
    assertEquals(expectedAssetDetail.getLocation(), result.getLocation());
    assertEquals(expectedAssetDetail.getAssignments(), result.getAssignments());

    verify(assetRepository, times(1)).findById(assetId);
  }

  @Test
  @DisplayName("should throw exception when trying to find non exist asset")
  void getAssetDetail_AssetNotExists_ThrowsException() {
    // Given
    Integer assetId = 1;

    when(assetRepository.findById(assetId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(AppException.class, () -> assetService.getAssetDetail(assetId));
  }

  @Test
  @DisplayName("should return with null returned date when assigned asset is not returned yet")
  void getAssetDetail_AssetAssigned_ReturnsNullReturnedDate() {
    // Given
    Integer assetId = 1;

    Asset mockedAsset = new Asset();
    mockedAsset.setId(assetId);
    mockedAsset.setName("Test Asset");
    mockedAsset.setSpecification("Test Specification");
    mockedAsset.setInstalledDate(LocalDate.now());

    Location location = new Location();
    location.setName("Test Location");
    mockedAsset.setLocation(location);

    Category category = new Category();
    category.setName("Test Category");
    mockedAsset.setCategory(category);

    Assignment assignment = new Assignment();
    assignment.setId(1);

    User assignee = new User();
    assignee.setId(1);
    assignee.setUsername("assignee");

    User assigner = new User();
    assigner.setId(2);
    assigner.setUsername("assigner");

    assignment.setAssignedBy(assigner);
    assignment.setAssignedTo(assignee);

    mockedAsset.setAssignments(List.of(assignment));

    when(assetRepository.findById(assetId)).thenReturn(Optional.of(mockedAsset));

    AssetDetailDtoResponse expectedResponse = new AssetDetailDtoResponse();
    expectedResponse.setId(assetId);
    expectedResponse.setName("Test Asset");
    expectedResponse.setSpecification("Test Specification");
    expectedResponse.setInstalledDate(LocalDate.now());
    expectedResponse.setLocation("Test Location");

    List<AssignmentDtoResponse> assignmentDtos = new ArrayList<>();
    AssignmentDtoResponse assignmentDto = new AssignmentDtoResponse();
    assignmentDto.setAssignedDate(LocalDate.now());
    assignmentDto.setReturnedDate(null);
    assignmentDto.setAssignedBy("assigner");
    assignmentDto.setAssignedTo("assignee");
    assignmentDtos.add(assignmentDto);
    expectedResponse.setAssignments(assignmentDtos);

    when(assetMapper.toDetailDto(mockedAsset)).thenReturn(expectedResponse);

    // When
    AssetDetailDtoResponse result = assetService.getAssetDetail(assetId);

    // Then
    assertNotNull(result);
    assertEquals(assetId, result.getId());
    assertEquals("Test Asset", result.getName());
    assertEquals("Test Specification", result.getSpecification());
    assertEquals("Test Location", result.getLocation());
    assertNotNull(result.getAssignments());
    assertEquals(1, result.getAssignments().size());
    assertNull(result.getAssignments().get(0).getReturnedDate());

    verify(assetRepository, times(1)).findById(assetId);
    verify(assetMapper, times(1)).toDetailDto(mockedAsset);
  }

  @Test
  @DisplayName("Test deleteAsset success when asset is valid")
  void testDeleteAsset_success() {
    when(assetRepository.findByIdAndDisabledFalse(1)).thenReturn(Optional.of(asset));
    when(assetRepository.existsAssignmentByAssetId(1)).thenReturn(false);

    assetService.deleteAsset(1);

    assertTrue(asset.getDisabled());
    verify(assetRepository, Mockito.times(1)).save(asset);
  }

  @Test
  @DisplayName("Test deleteAsset success with NOT_AVAILABLE status")
  void testDeleteAsset_successWithNotAvailableStatus() {
    // Given: Asset with NOT_AVAILABLE status, not assigned, no historical assignments
    asset.setStatus(AssetStatus.NOT_AVAILABLE);
    when(assetRepository.findByIdAndDisabledFalse(2)).thenReturn(Optional.of(asset));
    when(assetRepository.existsAssignmentByAssetId(2)).thenReturn(false);

    // When: Call deleteAsset
    assetService.deleteAsset(2);

    // Then: Verify the asset is marked as disabled and saved
    assertTrue(asset.getDisabled());
    verify(assetRepository, Mockito.times(1)).save(asset);
  }

  @Test
  @DisplayName("Test deleteAsset throws NotFoundException when asset not found")
  void testDeleteAsset_assetNotFound_throwsNotFoundException() {
    when(assetRepository.findByIdAndDisabledFalse(1)).thenReturn(Optional.empty());

    AppException exception = assertThrows(AppException.class, () -> assetService.deleteAsset(1));
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatusCode());
    assertEquals("Asset not found", exception.getMessage());
    verify(assetRepository, never()).save(any());
  }

  @Test
  @DisplayName("Test deleteAsset throws BadRequestException when asset is assigned")
  void testDeleteAsset_assetIsAssigned_throwsBadRequestException() {
    asset.setStatus(AssetStatus.ASSIGNED);
    when(assetRepository.findByIdAndDisabledFalse(1)).thenReturn(Optional.of(asset));

    AppException exception = assertThrows(AppException.class, () -> assetService.deleteAsset(1));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
    assertEquals("Cannot delete asset with Assigned status", exception.getMessage());
    verify(assetRepository, never()).save(any());
  }

  @Test
  @DisplayName("Test deleteAsset throws BadRequestException when asset has historical assignments")
  void testDeleteAsset_assetHasHistoricalAssignments_throwsBadRequestException() {
    when(assetRepository.findByIdAndDisabledFalse(1)).thenReturn(Optional.of(asset));
    when(assetRepository.existsAssignmentByAssetId(1)).thenReturn(true);

    AppException exception = assertThrows(AppException.class, () -> assetService.deleteAsset(1));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
    assertEquals(
        "Cannot delete the asset because it belongs to one or more historical assignments. "
            + "If the asset is not able to be used anymore, please update its state in Edit Asset page",
        exception.getMessage());
    verify(assetRepository, never()).save(any());
  }
}
