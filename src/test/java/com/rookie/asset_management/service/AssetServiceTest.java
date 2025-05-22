package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        assetService.searchFilterAndSortAssets(
            1, "laptop", "Laptop", List.of(AssetStatus.AVAILABLE), pageable);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getContent().size());

    List<ViewAssetListDtoResponse> content = new ArrayList<>(result.getContent());
    ViewAssetListDtoResponse response = content.get(0);

    assertEquals("LA0001", response.getAssetCode());
    assertEquals("Laptop Dell", response.getName());
    assertEquals("Laptop", response.getCategoryName());
    assertEquals(AssetStatus.AVAILABLE, response.getState());
    assertEquals("HCM", response.getLocationName());

    verify(assetRepository, times(1)).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void searchAssets_WithOnlyLocationId_ShouldReturnPagedAssets() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Asset> pageAssets = new PageImpl<>(List.of(asset), pageable, 1);

    when(assetRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageAssets);

    PagingDtoResponse<ViewAssetListDtoResponse> result =
        assetService.searchFilterAndSortAssets(1, null, null, null, pageable);

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
        assetService.searchFilterAndSortAssets(1, "laptop", null, null, pageable);

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
        assetService.searchFilterAndSortAssets(1, null, "Laptop", null, pageable);

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
        assetService.searchFilterAndSortAssets(
            1, null, null, List.of(AssetStatus.AVAILABLE), pageable);

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
        assetService.searchFilterAndSortAssets(1, "   ", "", List.of(), pageable);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    verify(assetRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void createNewAsset_Success() {
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

    when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
    when(userRepository.findById(1)).thenReturn(Optional.of(admin));
    when(assetRepository.existsByNameAndLocation("Laptop Dell", location)).thenReturn(false);
    when(assetRepository.save(any(Asset.class)))
        .thenAnswer(
            invocation -> {
              Asset a = invocation.getArgument(0);
              a.setId(123);
              return a;
            });

    CreateNewAssetDtoResponse response = assetService.createNewAsset(request, 1);

    assertNotNull(response);
    assertEquals("LA000123", response.getAssetCode());
    assertEquals("Laptop Dell", response.getName());
  }

  @Test
  void createNewAsset_InvalidState_ThrowsException() {
    CreateNewAssetDtoRequest request =
        CreateNewAssetDtoRequest.builder()
            .state(AssetStatus.ASSIGNED) // Không hợp lệ
            .build();

    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> assetService.createNewAsset(request, 1));

    assertEquals("Asset state must be AVAILABLE or NOT_AVAILABLE", ex.getMessage());
  }

  @Test
  void createNewAsset_EmptyName_ThrowsException() {
    CreateNewAssetDtoRequest request =
        CreateNewAssetDtoRequest.builder()
            .state(AssetStatus.AVAILABLE)
            .name(" ")
            .specification("spec")
            .installedDate(LocalDate.now())
            .categoryId(1)
            .build();

    AppException ex =
        assertThrows(AppException.class, () -> assetService.createNewAsset(request, 1));
    assertEquals("Asset name is required", ex.getMessage());
  }

  @Test
  void createNewAsset_PastInstalledDate_ThrowsException() {
    CreateNewAssetDtoRequest request =
        CreateNewAssetDtoRequest.builder()
            .name("Asset")
            .specification("spec")
            .installedDate(LocalDate.now().minusDays(1))
            .state(AssetStatus.AVAILABLE)
            .categoryId(1)
            .build();

    AppException ex =
        assertThrows(AppException.class, () -> assetService.createNewAsset(request, 1));
    assertEquals("Installed date must be today or a future date", ex.getMessage());
  }

  @Test
  void createNewAsset_DuplicateNameInLocation_ThrowsException() {
    CreateNewAssetDtoRequest request =
        CreateNewAssetDtoRequest.builder()
            .name("Laptop Dell")
            .specification("spec")
            .installedDate(LocalDate.now())
            .state(AssetStatus.AVAILABLE)
            .categoryId(1)
            .build();

    Location location = new Location();
    location.setId(1);
    User admin = new User();
    admin.setId(1);
    admin.setLocation(location);

    when(userRepository.findById(1)).thenReturn(Optional.of(admin));
    when(categoryRepository.findById(1)).thenReturn(Optional.of(new Category()));
    when(assetRepository.existsByNameAndLocation("Laptop Dell", location)).thenReturn(true);

    AppException ex =
        assertThrows(AppException.class, () -> assetService.createNewAsset(request, 1));
    assertTrue(ex.getMessage().contains("Asset name already exists in this location"));
  }

  @Test
  void createNewAsset_CategoryNotFound_ThrowsException() {
    CreateNewAssetDtoRequest request =
        CreateNewAssetDtoRequest.builder()
            .name("Asset")
            .specification("spec")
            .installedDate(LocalDate.now())
            .state(AssetStatus.AVAILABLE)
            .categoryId(99)
            .build();

    when(categoryRepository.findById(99)).thenReturn(Optional.empty());

    AppException ex =
        assertThrows(AppException.class, () -> assetService.createNewAsset(request, 1));
    assertEquals("Category not found", ex.getMessage());
  }

  @Test
  void createNewAsset_CategoryPrefixMissing_ThrowsException() {
    CreateNewAssetDtoRequest request =
        CreateNewAssetDtoRequest.builder()
            .name("Asset")
            .specification("spec")
            .installedDate(LocalDate.now())
            .state(AssetStatus.AVAILABLE)
            .categoryId(1)
            .build();

    Category category = new Category();
    category.setId(1);
    category.setPrefix(null);

    Location location = new Location();
    User admin = new User();
    admin.setLocation(location);

    when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
    when(userRepository.findById(1)).thenReturn(Optional.of(admin));
    when(assetRepository.existsByNameAndLocation("Asset", location)).thenReturn(false);
    when(assetRepository.save(any(Asset.class)))
        .thenAnswer(
            invocation -> {
              Asset a = invocation.getArgument(0);
              a.setId(123);
              return a;
            });

    AppException ex =
        assertThrows(AppException.class, () -> assetService.createNewAsset(request, 1));
    assertEquals("Category prefix is missing", ex.getMessage());
  }

  @Test
  void editAsset_Success() {
    Integer assetId = 1;
    Integer adminId = 2;

    EditAssetDtoRequest request =
        EditAssetDtoRequest.builder()
            .name("Updated Laptop")
            .specification("Updated Specs")
            .installedDate(LocalDate.now())
            .state(AssetStatus.NOT_AVAILABLE)
            .build();

    Location location = new Location();
    location.setId(1);

    Category category = new Category();
    category.setName("Laptop");

    User admin = new User();
    admin.setId(adminId);
    admin.setLocation(location);

    Asset asset = new Asset();
    asset.setId(assetId);
    asset.setName("Old Laptop");
    asset.setSpecification("Old Specs");
    asset.setInstalledDate(LocalDate.now().minusDays(1));
    asset.setStatus(AssetStatus.AVAILABLE);
    asset.setCategory(category);
    asset.setLocation(location);

    when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
    when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
    when(assetRepository.existsByNameAndLocation("Updated Laptop", location)).thenReturn(false);
    when(assetRepository.save(any(Asset.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    EditAssetDtoResponse response = assetService.editAsset(assetId, request, adminId);

    assertNotNull(response);
    assertEquals("Updated Laptop", response.getName());
    assertEquals("Updated Specs", response.getSpecification());
    assertEquals(AssetStatus.NOT_AVAILABLE, response.getState());
  }

  @Test
  void editAsset_AssetNotFound_ThrowsException() {
    when(assetRepository.findById(1)).thenReturn(Optional.empty());

    AppException ex =
        assertThrows(
            AppException.class, () -> assetService.editAsset(1, new EditAssetDtoRequest(), 1));

    assertEquals("Asset not found", ex.getMessage());
  }

  @Test
  void editAsset_AssetAssigned_ThrowsException() {
    Asset asset = new Asset();
    asset.setStatus(AssetStatus.ASSIGNED);

    when(assetRepository.findById(1)).thenReturn(Optional.of(asset));

    AppException ex =
        assertThrows(
            AppException.class, () -> assetService.editAsset(1, new EditAssetDtoRequest(), 1));

    assertEquals("Cannot edit assigned asset", ex.getMessage());
  }

  @Test
  void editAsset_MissingName_ThrowsException() {
    Asset asset = new Asset();
    asset.setStatus(AssetStatus.NOT_AVAILABLE);

    when(assetRepository.findById(1)).thenReturn(Optional.of(asset));

    EditAssetDtoRequest dto =
        EditAssetDtoRequest.builder()
            .name("  ")
            .specification("spec")
            .installedDate(LocalDate.now())
            .state(AssetStatus.AVAILABLE)
            .build();

    AppException ex = assertThrows(AppException.class, () -> assetService.editAsset(1, dto, 1));

    assertEquals("Asset name is required", ex.getMessage());
  }

  @Test
  void editAsset_MissingSpecification_ThrowsException() {
    Asset asset = new Asset();
    asset.setStatus(AssetStatus.AVAILABLE);

    when(assetRepository.findById(1)).thenReturn(Optional.of(asset));

    EditAssetDtoRequest dto =
        EditAssetDtoRequest.builder()
            .name("Asset")
            .specification(" ")
            .installedDate(LocalDate.now())
            .state(AssetStatus.AVAILABLE)
            .build();

    AppException ex = assertThrows(AppException.class, () -> assetService.editAsset(1, dto, 1));

    assertEquals("Specification is required", ex.getMessage());
  }

  @Test
  void editAsset_MissingInstalledDate_ThrowsException() {
    Asset asset = new Asset();
    asset.setStatus(AssetStatus.NOT_AVAILABLE);

    when(assetRepository.findById(1)).thenReturn(Optional.of(asset));

    EditAssetDtoRequest dto =
        EditAssetDtoRequest.builder()
            .name("Asset")
            .specification("Spec")
            .installedDate(null)
            .state(AssetStatus.AVAILABLE)
            .build();

    AppException ex = assertThrows(AppException.class, () -> assetService.editAsset(1, dto, 1));

    assertEquals("Installed date is required", ex.getMessage());
  }

  @Test
  void editAsset_PastInstalledDate_ThrowsException() {
    Asset asset = new Asset();
    asset.setStatus(AssetStatus.NOT_AVAILABLE);

    when(assetRepository.findById(1)).thenReturn(Optional.of(asset));

    EditAssetDtoRequest dto =
        EditAssetDtoRequest.builder()
            .name("Asset")
            .specification("Spec")
            .installedDate(LocalDate.now().minusDays(1))
            .state(AssetStatus.AVAILABLE)
            .build();

    AppException ex = assertThrows(AppException.class, () -> assetService.editAsset(1, dto, 1));

    assertEquals("Installed date must be today or a future date", ex.getMessage());
  }

  @Test
  void editAsset_InvalidState_ThrowsException() {
    Asset asset = new Asset();
    asset.setStatus(AssetStatus.AVAILABLE);

    when(assetRepository.findById(1)).thenReturn(Optional.of(asset));

    EditAssetDtoRequest dto =
        EditAssetDtoRequest.builder()
            .name("Asset")
            .specification("Spec")
            .installedDate(LocalDate.now())
            .state(null)
            .build();

    AppException ex = assertThrows(AppException.class, () -> assetService.editAsset(1, dto, 1));

    assertEquals("Invalid asset state", ex.getMessage());
  }

  @Test
  void editAsset_AdminNotFound_ThrowsException() {
    Asset asset = new Asset();
    asset.setStatus(AssetStatus.AVAILABLE);

    when(assetRepository.findById(1)).thenReturn(Optional.of(asset));
    when(userRepository.findById(1)).thenReturn(Optional.empty());

    EditAssetDtoRequest dto =
        EditAssetDtoRequest.builder()
            .name("Asset")
            .specification("Spec")
            .installedDate(LocalDate.now())
            .state(AssetStatus.AVAILABLE)
            .build();

    AppException ex = assertThrows(AppException.class, () -> assetService.editAsset(1, dto, 1));

    assertEquals("Admin not found", ex.getMessage());
  }

  @Test
  void editAsset_NameAlreadyExistsInLocation_ThrowsException() {
    Integer assetId = 1;
    Integer adminId = 2;

    Location location = new Location();
    location.setId(1);

    Asset asset = new Asset();
    asset.setId(assetId);
    asset.setName("Old Name");
    asset.setStatus(AssetStatus.AVAILABLE);
    asset.setLocation(location);

    User admin = new User();
    admin.setId(adminId);
    admin.setLocation(location);

    when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
    when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
    when(assetRepository.existsByNameAndLocation("New Name", location)).thenReturn(true);

    EditAssetDtoRequest dto =
        EditAssetDtoRequest.builder()
            .name("New Name")
            .specification("Spec")
            .installedDate(LocalDate.now())
            .state(AssetStatus.NOT_AVAILABLE)
            .build();

    AppException ex =
        assertThrows(AppException.class, () -> assetService.editAsset(assetId, dto, adminId));

    assertEquals("Asset name already exists in this location", ex.getMessage());
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
