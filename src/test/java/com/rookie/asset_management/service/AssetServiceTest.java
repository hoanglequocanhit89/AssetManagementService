package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import com.rookie.asset_management.entity.Asset;
import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.Category;
import com.rookie.asset_management.entity.Location;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.enums.AssetStatus;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.repository.AssetRepository;
import com.rookie.asset_management.repository.CategoryRepository;
import com.rookie.asset_management.repository.UserRepository;
import com.rookie.asset_management.service.impl.AssetServiceImpl;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

  @Mock private AssetRepository assetRepository;
  @Mock private CategoryRepository categoryRepository;
  @Mock private UserRepository userRepository;
  @InjectMocks private AssetServiceImpl assetService;
  @Mock private JwtService jwtService;

  private Asset asset;
  private Category category;
  private Location location;

  @BeforeEach
  void setUp() {
    category = new Category();
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

  @Test
  void searchFilterAndSortAssets_ShouldReturnPagedAssets() {
    // Arrange
    Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
    List<Asset> assets = List.of(asset);
    Page<Asset> pageAssets = new PageImpl<>(assets, pageable, 1);

    when(assetRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageAssets);

    // Act
    PagingDtoResponse<ViewAssetListDtoResponse> result =
        assetService.getAllAssets(1, "laptop", "Laptop", List.of(AssetStatus.AVAILABLE), pageable);

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
    Pageable pageable = PageRequest.of(0, 10);
    Page<Asset> pageAssets = new PageImpl<>(List.of(asset), pageable, 1);

    when(assetRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageAssets);

    PagingDtoResponse<ViewAssetListDtoResponse> result =
        assetService.getAllAssets(1, null, null, null, pageable);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    verify(assetRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void searchAssets_WithKeywordOnly_ShouldReturnPagedAssets() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Asset> pageAssets = new PageImpl<>(List.of(asset), pageable, 1);

    when(assetRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageAssets);

    PagingDtoResponse<ViewAssetListDtoResponse> result =
        assetService.getAllAssets(1, "laptop", null, null, pageable);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    verify(assetRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void searchAssets_WithCategoryOnly_ShouldReturnPagedAssets() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Asset> pageAssets = new PageImpl<>(List.of(asset), pageable, 1);

    when(assetRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageAssets);

    PagingDtoResponse<ViewAssetListDtoResponse> result =
        assetService.getAllAssets(1, null, "Laptop", null, pageable);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    verify(assetRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void searchAssets_WithStatesOnly_ShouldReturnPagedAssets() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Asset> pageAssets = new PageImpl<>(List.of(asset), pageable, 1);

    when(assetRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageAssets);

    PagingDtoResponse<ViewAssetListDtoResponse> result =
        assetService.getAllAssets(1, null, null, List.of(AssetStatus.AVAILABLE), pageable);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    verify(assetRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void searchAssets_WithBlankFilters_ShouldReturnPagedAssets() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Asset> pageAssets = new PageImpl<>(List.of(asset), pageable, 1);

    when(assetRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageAssets);

    PagingDtoResponse<ViewAssetListDtoResponse> result =
        assetService.getAllAssets(1, "   ", "", List.of(), pageable);

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

    Optional<Asset> mockAsset = Optional.of(asset);

    // Mock JWT service to return username
    when(jwtService.extractUsername()).thenReturn("admin1");
    when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
    when(userRepository.findByUsername("admin1")).thenReturn(Optional.of(admin));
    when(assetRepository.findByNameAndLocation("Laptop Dell", location))
        .thenReturn(Optional.empty());
    when(assetRepository.save(any(Asset.class)))
        .thenAnswer(
            invocation -> {
              Asset a = invocation.getArgument(0);
              if (a.getId() == null) {
                a.setId(123); // Set ID for first save (when assetCode is "PENDING")
              }
              return a;
            });

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
    verify(jwtService, times(1)).extractUsername();
    verify(categoryRepository, times(1)).findById(1);
    verify(userRepository, times(1)).findByUsername("admin1");
    verify(assetRepository, times(1)).findByNameAndLocation("Laptop Dell", location);
    verify(assetRepository, times(2))
        .save(
            any(Asset.class)); // Called twice: first for ID generation, second for assetCode update
  }

  @Test
  void createNewAsset_InvalidState_ThrowsException() {
    // Arrange
    CreateNewAssetDtoRequest request =
        CreateNewAssetDtoRequest.builder()
            .name("Laptop Dell")
            .specification("i7, 16GB RAM")
            .installedDate(LocalDate.now())
            .state(AssetStatus.ASSIGNED) // Invalid state
            .categoryId(1)
            .build();

    // Act & Assert
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> assetService.createNewAsset(request));

    assertEquals("Asset state must be AVAILABLE or NOT_AVAILABLE", ex.getMessage());

    // Verify no interactions with repositories since validation fails early
    verify(jwtService, never()).extractUsername();
    verify(categoryRepository, never()).findById(any());
    verify(userRepository, never()).findByUsername(any());
    verify(assetRepository, never()).save(any());
  }

  @Test
  void createNewAsset_EmptyName_ThrowsException() {
    // Arrange
    CreateNewAssetDtoRequest request =
        CreateNewAssetDtoRequest.builder()
            .name("   ") // Empty name
            .specification("i7, 16GB RAM")
            .installedDate(LocalDate.now())
            .state(AssetStatus.AVAILABLE)
            .categoryId(1)
            .build();

    // Act & Assert
    AppException ex = assertThrows(AppException.class, () -> assetService.createNewAsset(request));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatusCode());
    assertEquals("Asset name is required", ex.getMessage());

    // Verify no interactions with repositories since validation fails early
    verify(jwtService, never()).extractUsername();
    verify(categoryRepository, never()).findById(any());
    verify(userRepository, never()).findByUsername(any());
    verify(assetRepository, never()).save(any());
  }

  @Test
  void createNewAsset_EmptySpecification_ThrowsException() {
    // Arrange
    CreateNewAssetDtoRequest request =
        CreateNewAssetDtoRequest.builder()
            .name("Laptop Dell")
            .specification("   ") // Empty specification
            .installedDate(LocalDate.now())
            .state(AssetStatus.AVAILABLE)
            .categoryId(1)
            .build();

    // Act & Assert
    AppException ex = assertThrows(AppException.class, () -> assetService.createNewAsset(request));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatusCode());
    assertEquals("Specification is required", ex.getMessage());

    // Verify no interactions with repositories since validation fails early
    verify(jwtService, never()).extractUsername();
    verify(categoryRepository, never()).findById(any());
    verify(userRepository, never()).findByUsername(any());
    verify(assetRepository, never()).save(any());
  }

  @Test
  void createNewAsset_NullInstalledDate_ThrowsException() {
    // Arrange
    CreateNewAssetDtoRequest request =
        CreateNewAssetDtoRequest.builder()
            .name("Laptop Dell")
            .specification("i7, 16GB RAM")
            .installedDate(null) // Null installed date
            .state(AssetStatus.AVAILABLE)
            .categoryId(1)
            .build();

    // Act & Assert
    AppException ex = assertThrows(AppException.class, () -> assetService.createNewAsset(request));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatusCode());
    assertEquals("Installed date is required", ex.getMessage());

    // Verify no interactions with repositories since validation fails early
    verify(jwtService, never()).extractUsername();
    verify(categoryRepository, never()).findById(any());
    verify(userRepository, never()).findByUsername(any());
    verify(assetRepository, never()).save(any());
  }

  @Test
  void createNewAsset_NullCategoryId_ThrowsException() {
    // Arrange
    CreateNewAssetDtoRequest request =
        CreateNewAssetDtoRequest.builder()
            .name("Laptop Dell")
            .specification("i7, 16GB RAM")
            .installedDate(LocalDate.now())
            .state(AssetStatus.AVAILABLE)
            .categoryId(null) // Null category ID
            .build();

    // Act & Assert
    AppException ex = assertThrows(AppException.class, () -> assetService.createNewAsset(request));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatusCode());
    assertEquals("Category ID is required", ex.getMessage());

    // Verify no interactions with repositories since validation fails early
    verify(jwtService, never()).extractUsername();
    verify(categoryRepository, never()).findById(any());
    verify(userRepository, never()).findByUsername(any());
    verify(assetRepository, never()).save(any());
  }

  @Test
  void createNewAsset_CategoryNotFound_ThrowsException() {
    // Arrange
    CreateNewAssetDtoRequest request =
        CreateNewAssetDtoRequest.builder()
            .name("Laptop Dell")
            .specification("i7, 16GB RAM")
            .installedDate(LocalDate.now())
            .state(AssetStatus.AVAILABLE)
            .categoryId(999) // Non-existent category
            .build();

    when(categoryRepository.findById(999)).thenReturn(Optional.empty());

    // Act & Assert
    AppException ex = assertThrows(AppException.class, () -> assetService.createNewAsset(request));

    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatusCode());
    assertEquals("Category not found", ex.getMessage());

    // Verify limited interactions since category lookup fails
    verify(jwtService, never()).extractUsername();
    verify(categoryRepository, times(1)).findById(999);
    verify(userRepository, never()).findByUsername(any());
    verify(assetRepository, never()).save(any());
  }

  @Test
  void createNewAsset_UserNotFound_ThrowsException() {
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

    when(jwtService.extractUsername()).thenReturn("nonexistent_user");
    when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
    when(userRepository.findByUsername("nonexistent_user")).thenReturn(Optional.empty());

    // Act & Assert
    AppException ex = assertThrows(AppException.class, () -> assetService.createNewAsset(request));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatusCode());
    assertEquals("User Not Found", ex.getMessage());

    // Verify interactions up to user lookup
    verify(jwtService, times(1)).extractUsername();
    verify(categoryRepository, times(1)).findById(1);
    verify(userRepository, times(1)).findByUsername("nonexistent_user");
    verify(assetRepository, never()).save(any());
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

    when(jwtService.extractUsername()).thenReturn("admin1");
    when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
    when(userRepository.findByUsername("admin1")).thenReturn(Optional.of(admin));
    Asset existingAsset = new Asset();
    existingAsset.setDisabled(false); // simulate an active (not deleted) asset
    when(assetRepository.findByNameAndLocation("Laptop Dell", location))
        .thenReturn(Optional.of(existingAsset));

    // Act & Assert
    AppException ex = assertThrows(AppException.class, () -> assetService.createNewAsset(request));

    assertEquals(HttpStatus.CONFLICT, ex.getHttpStatusCode());
    assertTrue(ex.getMessage().contains("Asset name already exists in this location"));

    // Verify all interactions up to duplicate check
    verify(jwtService, times(1)).extractUsername();
    verify(categoryRepository, times(1)).findById(1);
    verify(userRepository, times(1)).findByUsername("admin1");
    verify(assetRepository, times(1)).findByNameAndLocation("Laptop Dell", location);
    verify(assetRepository, never()).save(any());
  }

  @Test
  void createNewAsset_CategoryPrefixMissing_ThrowsException() {
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
    category.setPrefix(null); // Missing prefix

    Location location = new Location();
    location.setId(1);
    location.setName("HCM");

    User admin = new User();
    admin.setId(1);
    admin.setUsername("admin1");
    admin.setLocation(location);

    Asset asset = new Asset();
    asset.setName("Laptop Dell");
    asset.setLocation(location);
    asset.setDisabled(false);
    Optional<Asset> mockAsset = Optional.of(asset);

    when(jwtService.extractUsername()).thenReturn("admin1");
    when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
    when(userRepository.findByUsername("admin1")).thenReturn(Optional.of(admin));
    when(assetRepository.findByNameAndLocation("Laptop Dell", location))
        .thenReturn(Optional.empty());
    when(assetRepository.save(any(Asset.class)))
        .thenAnswer(
            invocation -> {
              Asset a = invocation.getArgument(0);
              a.setId(123); // Set ID for first save
              return a;
            });

    // Act & Assert
    AppException ex = assertThrows(AppException.class, () -> assetService.createNewAsset(request));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatusCode());
    assertEquals("Category prefix is missing", ex.getMessage());

    // Verify interactions up to the point where prefix is checked
    verify(jwtService, times(1)).extractUsername();
    verify(categoryRepository, times(1)).findById(1);
    verify(userRepository, times(1)).findByUsername("admin1");
    verify(assetRepository, times(1)).findByNameAndLocation("Laptop Dell", location);
    verify(assetRepository, times(1)).save(any(Asset.class)); // First save to get ID
  }

  @Test
  void createNewAsset_EmptyPrefix_ThrowsException() {
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
    category.setPrefix("   "); // Empty prefix

    Location location = new Location();
    location.setId(1);
    location.setName("HCM");

    User admin = new User();
    admin.setId(1);
    admin.setUsername("admin1");
    admin.setLocation(location);

    Asset asset = new Asset();
    asset.setName("Laptop Dell");
    asset.setLocation(location);
    asset.setDisabled(false);
    Optional<Asset> mockAsset = Optional.of(asset);

    when(jwtService.extractUsername()).thenReturn("admin1");
    when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
    when(userRepository.findByUsername("admin1")).thenReturn(Optional.of(admin));
    when(assetRepository.findByNameAndLocation("Laptop Dell", location))
        .thenReturn(Optional.empty());
    when(assetRepository.save(any(Asset.class)))
        .thenAnswer(
            invocation -> {
              Asset a = invocation.getArgument(0);
              a.setId(123);
              return a;
            });

    // Act & Assert
    AppException ex = assertThrows(AppException.class, () -> assetService.createNewAsset(request));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatusCode());
    assertEquals("Category prefix is missing", ex.getMessage());
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

    Asset asset = new Asset();
    asset.setName("Laptop Dell");
    asset.setLocation(location);
    asset.setDisabled(false);
    Optional<Asset> mockAsset = Optional.of(asset);

    when(jwtService.extractUsername()).thenReturn("admin1");
    when(userRepository.findByUsername("admin1")).thenReturn(Optional.of(admin));
    when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
    when(assetRepository.findByNameAndLocation("Laptop Dell", location))
        .thenReturn(Optional.empty());
    when(assetRepository.save(any(Asset.class)))
        .thenAnswer(
            invocation -> {
              Asset a = invocation.getArgument(0);
              if (a.getId() == null) {
                a.setId(123);
              }
              return a;
            });

    // Act
    CreateNewAssetDtoResponse response = assetService.createNewAsset(request);

    // Assert
    assertNotNull(response);
    assertEquals(AssetStatus.NOT_AVAILABLE, response.getState());
    assertEquals("LA000123", response.getAssetCode());
    assertEquals("Laptop Dell", response.getName());

    verify(assetRepository, times(2)).save(any(Asset.class));
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
    Optional<Asset> mockAsset = Optional.of(asset);

    when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
    when(jwtService.extractUsername()).thenReturn("admin1");
    when(userRepository.findByUsername("admin1")).thenReturn(Optional.of(admin));
    when(assetRepository.findByNameAndLocationAndIdNot("Updated Laptop", location, assetId))
        .thenReturn(Optional.empty());
    when(assetRepository.save(any(Asset.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

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
    verify(jwtService, times(1)).extractUsername();
    verify(userRepository, times(1)).findByUsername("admin1");
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
    verify(jwtService, never()).extractUsername();
    verify(userRepository, never()).findByUsername(any());
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
    verify(jwtService, never()).extractUsername();
    verify(userRepository, never()).findByUsername(any());
    verify(assetRepository, never()).save(any());
  }

  @Test
  void editAsset_MissingName_ThrowsException() {
    // Arrange
    Integer assetId = 1;
    Asset asset = new Asset();
    asset.setId(assetId);
    asset.setStatus(AssetStatus.AVAILABLE);

    EditAssetDtoRequest request =
        EditAssetDtoRequest.builder()
            .name("   ") // Empty name
            .specification("Test Spec")
            .installedDate(LocalDate.now())
            .state(AssetStatus.AVAILABLE)
            .build();

    when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));

    // Act & Assert
    AppException ex =
        assertThrows(AppException.class, () -> assetService.editAsset(assetId, request));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatusCode());
    assertEquals("Asset name is required", ex.getMessage());

    // Verify no further interactions
    verify(assetRepository, times(1)).findById(assetId);
    verify(jwtService, never()).extractUsername();
    verify(userRepository, never()).findByUsername(any());
    verify(assetRepository, never()).save(any());
  }

  @Test
  void editAsset_MissingSpecification_ThrowsException() {
    // Arrange
    Integer assetId = 1;
    Asset asset = new Asset();
    asset.setId(assetId);
    asset.setStatus(AssetStatus.AVAILABLE);

    EditAssetDtoRequest request =
        EditAssetDtoRequest.builder()
            .name("Test Asset")
            .specification("  ") // Empty specification
            .installedDate(LocalDate.now())
            .state(AssetStatus.AVAILABLE)
            .build();

    when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));

    // Act & Assert
    AppException ex =
        assertThrows(AppException.class, () -> assetService.editAsset(assetId, request));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatusCode());
    assertEquals("Specification is required", ex.getMessage());

    // Verify no further interactions
    verify(assetRepository, times(1)).findById(assetId);
    verify(jwtService, never()).extractUsername();
    verify(userRepository, never()).findByUsername(any());
    verify(assetRepository, never()).save(any());
  }

  @Test
  void editAsset_MissingInstalledDate_ThrowsException() {
    // Arrange
    Integer assetId = 1;
    Asset asset = new Asset();
    asset.setId(assetId);
    asset.setStatus(AssetStatus.AVAILABLE);

    EditAssetDtoRequest request =
        EditAssetDtoRequest.builder()
            .name("Test Asset")
            .specification("Test Spec")
            .installedDate(null) // Null installed date
            .state(AssetStatus.AVAILABLE)
            .build();

    when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));

    // Act & Assert
    AppException ex =
        assertThrows(AppException.class, () -> assetService.editAsset(assetId, request));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatusCode());
    assertEquals("Installed date is required", ex.getMessage());

    // Verify no further interactions
    verify(assetRepository, times(1)).findById(assetId);
    verify(jwtService, never()).extractUsername();
    verify(userRepository, never()).findByUsername(any());
    verify(assetRepository, never()).save(any());
  }

  @Test
  void editAsset_InvalidState_ThrowsException() {
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
            .state(null) // Invalid state
            .build();

    when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));

    // Act & Assert
    AppException ex =
        assertThrows(AppException.class, () -> assetService.editAsset(assetId, request));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatusCode());
    assertEquals("Invalid asset state", ex.getMessage());

    // Verify no further interactions
    verify(assetRepository, times(1)).findById(assetId);
    verify(jwtService, never()).extractUsername();
    verify(userRepository, never()).findByUsername(any());
    verify(assetRepository, never()).save(any());
  }

  @Test
  void editAsset_InvalidStateAssigned_ThrowsException() {
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
            .state(AssetStatus.ASSIGNED) // Invalid state for editing
            .build();

    when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));

    // Act & Assert
    AppException ex =
        assertThrows(AppException.class, () -> assetService.editAsset(assetId, request));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatusCode());
    assertEquals("Invalid asset state", ex.getMessage());

    // Verify no further interactions
    verify(assetRepository, times(1)).findById(assetId);
    verify(jwtService, never()).extractUsername();
    verify(userRepository, never()).findByUsername(any());
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
    when(jwtService.extractUsername()).thenReturn("nonexistent_user");
    when(userRepository.findByUsername("nonexistent_user")).thenReturn(Optional.empty());

    // Act & Assert
    AppException ex =
        assertThrows(AppException.class, () -> assetService.editAsset(assetId, request));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatusCode());
    assertEquals("User Not Found", ex.getMessage());

    // Verify interactions up to user lookup
    verify(assetRepository, times(1)).findById(assetId);
    verify(jwtService, times(1)).extractUsername();
    verify(userRepository, times(1)).findByUsername("nonexistent_user");
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

    Asset asset = new Asset();
    asset.setId(assetId);
    asset.setName("Old Asset Name");
    asset.setStatus(AssetStatus.AVAILABLE);
    asset.setLocation(location);
    asset.setCategory(category);
    asset.setDisabled(false);

    Optional<Asset> mockAsset = Optional.of(asset);

    EditAssetDtoRequest request =
        EditAssetDtoRequest.builder()
            .name("Existing Asset Name") // Different from current name
            .specification("Test Spec")
            .installedDate(LocalDate.now())
            .state(AssetStatus.NOT_AVAILABLE)
            .build();

    when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
    when(jwtService.extractUsername()).thenReturn("admin1");
    when(userRepository.findByUsername("admin1")).thenReturn(Optional.of(admin));
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
    verify(jwtService, times(1)).extractUsername();
    verify(userRepository, times(1)).findByUsername("admin1");
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
    when(jwtService.extractUsername()).thenReturn("admin1");
    when(userRepository.findByUsername("admin1")).thenReturn(Optional.of(admin));
    when(assetRepository.save(any(Asset.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    EditAssetDtoResponse response = assetService.editAsset(assetId, request);

    // Assert
    assertNotNull(response);
    assertEquals(AssetStatus.WAITING, response.getState());

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
    when(jwtService.extractUsername()).thenReturn("admin1");
    when(userRepository.findByUsername("admin1")).thenReturn(Optional.of(admin));
    when(assetRepository.save(any(Asset.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    EditAssetDtoResponse response = assetService.editAsset(assetId, request);

    // Assert
    assertNotNull(response);
    assertEquals(AssetStatus.RECYCLED, response.getState());

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

    AssetDetailDtoResponse result = assetService.getAssetDetail(assetId);

    assertEquals(expectedAssetDetail.getId(), result.getId());
    assertEquals(expectedAssetDetail.getName(), result.getName());
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

    // When
    AssetDetailDtoResponse result = assetService.getAssetDetail(assetId);

    // Then
    assertEquals(null, result.getAssignments().get(0).getReturnedDate());
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
