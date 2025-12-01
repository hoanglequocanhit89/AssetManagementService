package com.rookie.asset_management.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.return_request.CompleteReturningRequestDtoResponse;
import com.rookie.asset_management.dto.response.return_request.ReturningRequestDtoResponse;
import com.rookie.asset_management.dto.response.returning.ReturningRequestDetailDtoResponse;
import com.rookie.asset_management.service.ReturningRequestService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ReturningRequestControllerTest {

  @Mock private ReturningRequestService returningRequestService;

  @InjectMocks private ReturningRequestController returningRequestController;

  @Nested
  @DisplayName("getAllReturningRequests Tests")
  class GetAllReturningRequestsTest {

    @Test
    @DisplayName("should return paginated returning requests successfully")
    void getAllReturningRequests_Success_ReturnsPaginatedRequests() {
      ReturningRequestDtoResponse request = new ReturningRequestDtoResponse();
      request.setAssetCode("LA000001");
      request.setAssetName("Laptop Dell");

      PagingDtoResponse<ReturningRequestDtoResponse> pagingResponse = new PagingDtoResponse<>();
      pagingResponse.setContent(List.of(request));

      when(returningRequestService.getAllReturningRequests(
              any(), any(), any(), eq(0), eq(20), eq("assetCode"), eq("asc")))
          .thenReturn(pagingResponse);

      ResponseEntity<ApiDtoResponse<PagingDtoResponse<ReturningRequestDtoResponse>>> response =
          returningRequestController.getAllReturningRequests(
              null, null, null, 0, 20, "assetCode", "asc");

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Returning requests retrieved successfully", response.getBody().getMessage());
      assertNotNull(response.getBody().getData());
    }
  }

  @Nested
  @DisplayName("completeReturningRequest Tests")
  class CompleteReturningRequestTest {

    @Test
    @DisplayName("should complete returning request successfully")
    void completeReturningRequest_Success_ReturnsOk() {
      CompleteReturningRequestDtoResponse completeResponse =
          CompleteReturningRequestDtoResponse.builder().id(1).status("COMPLETED").build();

      when(returningRequestService.completeReturningRequest(1)).thenReturn(completeResponse);

      ResponseEntity<ApiDtoResponse<Void>> response =
          returningRequestController.completeReturningRequest(1);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Returning request completed successfully", response.getBody().getMessage());
    }

    @Test
    @DisplayName("should return already completed message when request is null")
    void completeReturningRequest_AlreadyCompleted_ReturnsMessage() {
      when(returningRequestService.completeReturningRequest(1)).thenReturn(null);

      ResponseEntity<ApiDtoResponse<Void>> response =
          returningRequestController.completeReturningRequest(1);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Request has been completed already", response.getBody().getMessage());
    }
  }

  @Nested
  @DisplayName("createReturningRequest Tests")
  class CreateReturningRequestTest {

    @Test
    @DisplayName("should create returning request successfully")
    void createReturningRequest_Success_ReturnsCreated() {
      ReturningRequestDetailDtoResponse detailResponse = new ReturningRequestDetailDtoResponse();
      detailResponse.setAssetCode("LA000001");

      when(returningRequestService.createReturningRequest(1)).thenReturn(detailResponse);

      ResponseEntity<ApiDtoResponse<ReturningRequestDetailDtoResponse>> response =
          returningRequestController.createReturningRequest(1);

      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Returning request created successfully.", response.getBody().getMessage());
    }
  }

  @Nested
  @DisplayName("createUserReturningRequest Tests")
  class CreateUserReturningRequestTest {

    @Test
    @DisplayName("should create user returning request successfully")
    void createUserReturningRequest_Success_ReturnsCreated() {
      ReturningRequestDetailDtoResponse detailResponse = new ReturningRequestDetailDtoResponse();
      detailResponse.setAssetCode("LA000001");

      when(returningRequestService.createUserReturningRequest(1)).thenReturn(detailResponse);

      ResponseEntity<ApiDtoResponse<ReturningRequestDetailDtoResponse>> response =
          returningRequestController.createUserReturningRequest(1);

      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals(
          "Returning request created successfully by user.", response.getBody().getMessage());
    }
  }

  @Nested
  @DisplayName("cancelReturningRequest Tests")
  class CancelReturningRequestTest {

    @Test
    @DisplayName("should cancel returning request successfully")
    void cancelReturningRequest_Success_ReturnsOk() {
      ReturningRequestDetailDtoResponse detailResponse = new ReturningRequestDetailDtoResponse();
      detailResponse.setAssetCode("LA000001");

      when(returningRequestService.cancelReturningRequest(1)).thenReturn(detailResponse);

      ResponseEntity<ApiDtoResponse<ReturningRequestDetailDtoResponse>> response =
          returningRequestController.cancelReturningRequest(1);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Returning request cancelled successfully.", response.getBody().getMessage());
    }
  }
}
