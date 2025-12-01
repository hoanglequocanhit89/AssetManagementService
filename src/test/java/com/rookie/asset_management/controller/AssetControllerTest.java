package com.rookie.asset_management.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.rookie.asset_management.dto.request.asset.CreateNewAssetDtoRequest;
import com.rookie.asset_management.dto.request.asset.EditAssetDtoRequest;
import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.asset.AssetBriefDtoResponse;
import com.rookie.asset_management.dto.response.asset.AssetDetailDtoResponse;
import com.rookie.asset_management.dto.response.asset.CreateNewAssetDtoResponse;
import com.rookie.asset_management.dto.response.asset.EditAssetDtoResponse;
import com.rookie.asset_management.dto.response.asset.ViewAssetListDtoResponse;
import com.rookie.asset_management.enums.AssetStatus;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.service.AssetService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AssetControllerTest {

  @Mock private AssetService assetService;

  @InjectMocks private AssetController assetController;

  @Nested
  @DisplayName("getAssetsByFilterSearchAndSort Tests")
  class GetAssetsByFilterSearchAndSortTest {

    @Test
    @DisplayName("should return assets with ascending sort")
    void getAssets_AscendingSort_ReturnsAssets() {
      PagingDtoResponse<ViewAssetListDtoResponse> pagingResponse = new PagingDtoResponse<>();
      pagingResponse.setContent(List.of(new ViewAssetListDtoResponse()));

      when(assetService.getAllAssets(any(), any(), any(), any(Pageable.class)))
          .thenReturn(pagingResponse);

      ResponseEntity<ApiDtoResponse<PagingDtoResponse<ViewAssetListDtoResponse>>> response =
          assetController.getAssetsByFilterSearchAndSort(
              "laptop", "Laptop", List.of(AssetStatus.AVAILABLE), 0, 20, "assetCode", "asc");

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Assets retrieved successfully.", response.getBody().getMessage());
      assertNotNull(response.getBody().getData());
    }

    @Test
    @DisplayName("should return assets with descending sort")
    void getAssets_DescendingSort_ReturnsAssets() {
      PagingDtoResponse<ViewAssetListDtoResponse> pagingResponse = new PagingDtoResponse<>();
      pagingResponse.setContent(List.of(new ViewAssetListDtoResponse()));

      when(assetService.getAllAssets(any(), any(), any(), any(Pageable.class)))
          .thenReturn(pagingResponse);

      ResponseEntity<ApiDtoResponse<PagingDtoResponse<ViewAssetListDtoResponse>>> response =
          assetController.getAssetsByFilterSearchAndSort(null, null, null, 0, 20, "name", "desc");

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      verify(assetService).getAllAssets(eq(null), eq(null), eq(null), any(Pageable.class));
    }
  }

  @Nested
  @DisplayName("createAsset Tests")
  class CreateAssetTest {

    @Test
    @DisplayName("should create asset successfully")
    void createAsset_Success_ReturnsCreated() {
      CreateNewAssetDtoRequest request =
          CreateNewAssetDtoRequest.builder()
              .name("Laptop Dell")
              .specification("i7, 16GB RAM")
              .installedDate(LocalDate.now())
              .state(AssetStatus.AVAILABLE)
              .categoryId(1)
              .build();

      CreateNewAssetDtoResponse serviceResponse =
          CreateNewAssetDtoResponse.builder()
              .id(1)
              .assetCode("LA000001")
              .name("Laptop Dell")
              .build();

      when(assetService.createNewAsset(any(CreateNewAssetDtoRequest.class)))
          .thenReturn(serviceResponse);

      ResponseEntity<ApiDtoResponse<CreateNewAssetDtoResponse>> response =
          assetController.createAsset(request);

      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Asset created successfully.", response.getBody().getMessage());
      assertEquals("LA000001", response.getBody().getData().getAssetCode());
    }
  }

  @Nested
  @DisplayName("editAsset Tests")
  class EditAssetTest {

    @Test
    @DisplayName("should edit asset successfully")
    void editAsset_Success_ReturnsOk() {
      EditAssetDtoRequest request =
          EditAssetDtoRequest.builder()
              .name("Updated Laptop")
              .specification("i9, 32GB RAM")
              .installedDate(LocalDate.now())
              .state(AssetStatus.AVAILABLE)
              .build();

      EditAssetDtoResponse serviceResponse =
          EditAssetDtoResponse.builder().id(1).assetCode("LA000001").name("Updated Laptop").build();

      when(assetService.editAsset(eq(1), any(EditAssetDtoRequest.class)))
          .thenReturn(serviceResponse);

      ResponseEntity<ApiDtoResponse<EditAssetDtoResponse>> response =
          assetController.editAsset(1, request);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Asset updated successfully.", response.getBody().getMessage());
      assertEquals("Updated Laptop", response.getBody().getData().getName());
    }
  }

  @Nested
  @DisplayName("deleteAsset Tests")
  class DeleteAssetTest {

    @Test
    @DisplayName("should delete asset successfully")
    void deleteAsset_Success_ReturnsOk() {
      doNothing().when(assetService).deleteAsset(1);

      ResponseEntity<ApiDtoResponse<String>> response = assetController.deleteAsset(1);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Asset deleted successfully.", response.getBody().getMessage());
      verify(assetService).deleteAsset(1);
    }

    @Test
    @DisplayName("should return bad request when asset cannot be deleted")
    void deleteAsset_BadRequest_ReturnsBadRequest() {
      doThrow(new AppException(HttpStatus.BAD_REQUEST, "Asset is currently assigned"))
          .when(assetService)
          .deleteAsset(1);

      ResponseEntity<ApiDtoResponse<String>> response = assetController.deleteAsset(1);

      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Asset is currently assigned", response.getBody().getMessage());
    }

    @Test
    @DisplayName("should return not found when asset does not exist")
    void deleteAsset_NotFound_ReturnsNotFound() {
      doThrow(new AppException(HttpStatus.NOT_FOUND, "Asset not found"))
          .when(assetService)
          .deleteAsset(999);

      ResponseEntity<ApiDtoResponse<String>> response = assetController.deleteAsset(999);

      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Asset not found.", response.getBody().getMessage());
    }
  }

  @Nested
  @DisplayName("getAssetDetails Tests")
  class GetAssetDetailsTest {

    @Test
    @DisplayName("should return asset details successfully")
    void getAssetDetails_Success_ReturnsAssetDetails() {
      AssetDetailDtoResponse detailResponse = new AssetDetailDtoResponse();
      detailResponse.setAssetCode("LA000001");
      detailResponse.setName("Laptop Dell");

      when(assetService.getAssetDetail(1)).thenReturn(detailResponse);

      ResponseEntity<ApiDtoResponse<AssetDetailDtoResponse>> response =
          assetController.getAssetDetails(1);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Asset details retrieved successfully.", response.getBody().getMessage());
      assertEquals("LA000001", response.getBody().getData().getAssetCode());
    }
  }

  @Nested
  @DisplayName("getAllAssetBrief Tests")
  class GetAllAssetBriefTest {

    @Test
    @DisplayName("should return asset briefs successfully")
    void getAllAssetBrief_Success_ReturnsAssetBriefs() {
      AssetBriefDtoResponse brief =
          AssetBriefDtoResponse.builder()
              .id(1)
              .assetCode("LA000001")
              .assetName("Laptop Dell")
              .categoryName("Laptop")
              .build();

      when(assetService.getAllAvailableAssetBrief("laptop", "assetCode", "asc"))
          .thenReturn(List.of(brief));

      ResponseEntity<ApiDtoResponse<List<AssetBriefDtoResponse>>> response =
          assetController.getAllAssetBrief("laptop", "assetCode", "asc");

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Assets retrieved successfully.", response.getBody().getMessage());
      assertEquals(1, response.getBody().getData().size());
    }
  }
}
