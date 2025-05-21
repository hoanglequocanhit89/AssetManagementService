package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.rookie.asset_management.entity.Asset;
import com.rookie.asset_management.enums.AssetStatus;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.repository.AssetRepository;
import com.rookie.asset_management.repository.CategoryRepository;
import com.rookie.asset_management.repository.UserRepository;
import com.rookie.asset_management.service.impl.AssetServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

/**
 * This class is a test suite for AssetServiceImpl, focusing on the deleteAsset functionality. This
 * should not be a public class, use the default instead. The test class should be annotated with
 * <b>@ExtendWith(MockitoExtension.class)</b> to enable Mockito support.
 */
@ExtendWith(MockitoExtension.class)
class AssetServiceTest {
  @InjectMocks private AssetServiceImpl assetService;

  @Mock private AssetRepository assetRepository;
  @Mock private CategoryRepository categoryRepository;
  @Mock private UserRepository userRepository;

  private Asset asset;

  @BeforeEach
  void setUp() {
    asset = new Asset();
    asset.setId(1);
    asset.setName("Laptop Dell");
    asset.setSpecification("Core i7, 16GB RAM");
    asset.setStatus(AssetStatus.AVAILABLE);
    asset.setDisabled(false);
  }

  @Test
  @DisplayName("Test deleteAsset success when asset is valid")
  void testDeleteAsset_success() {
    when(assetRepository.findByIdAndDisabledFalse(1)).thenReturn(Optional.of(asset));
    when(assetRepository.existsAssignmentByAssetId(1)).thenReturn(false);

    assetService.deleteAsset(1);

    assertTrue(asset.getDisabled());
    verify(assetRepository, times(1)).save(asset);
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
    verify(assetRepository, times(1)).save(asset);
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
