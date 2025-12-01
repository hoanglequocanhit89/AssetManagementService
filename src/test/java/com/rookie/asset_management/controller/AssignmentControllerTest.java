package com.rookie.asset_management.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.rookie.asset_management.dto.request.assignment.CreateUpdateAssignmentRequest;
import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.assignment.AssignmentDetailDtoResponse;
import com.rookie.asset_management.dto.response.assignment.AssignmentDetailForEditResponse;
import com.rookie.asset_management.dto.response.assignment.AssignmentListDtoResponse;
import com.rookie.asset_management.dto.response.assignment.AssignmentStatusResponse;
import com.rookie.asset_management.dto.response.assignment.MyAssignmentDtoResponse;
import com.rookie.asset_management.enums.AssignmentStatus;
import com.rookie.asset_management.service.AssignmentService;
import java.time.LocalDate;
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
class AssignmentControllerTest {

  @Mock private AssignmentService assignmentService;

  @InjectMocks private AssignmentController assignmentController;

  @Nested
  @DisplayName("getAllAssignments Tests")
  class GetAllAssignmentsTest {

    @Test
    @DisplayName("should return paginated assignments successfully")
    void getAllAssignments_Success_ReturnsPaginatedAssignments() {
      AssignmentListDtoResponse assignment =
          AssignmentListDtoResponse.builder()
              .id(1)
              .assetCode("LA000001")
              .assetName("Laptop Dell")
              .assignedTo("John Doe")
              .assignedBy("Admin User")
              .assignedDate(LocalDate.now())
              .status("WAITING")
              .build();

      PagingDtoResponse<AssignmentListDtoResponse> pagingResponse = new PagingDtoResponse<>();
      pagingResponse.setContent(List.of(assignment));

      when(assignmentService.getAllAssignments(
              any(), any(), any(), eq(0), eq(20), eq("assetCode"), eq("asc")))
          .thenReturn(pagingResponse);

      ResponseEntity<ApiDtoResponse<PagingDtoResponse<AssignmentListDtoResponse>>> response =
          assignmentController.getAllAssignments(null, null, null, 0, 20, "assetCode", "asc");

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Assignments retrieved successfully", response.getBody().getMessage());
      assertNotNull(response.getBody().getData());
      verify(assignmentService)
          .getAllAssignments(any(), any(), any(), eq(0), eq(20), eq("assetCode"), eq("asc"));
    }
  }

  @Nested
  @DisplayName("createAssignment Tests")
  class CreateAssignmentTest {

    @Test
    @DisplayName("should create assignment successfully")
    void createAssignment_Success_ReturnsCreated() {
      CreateUpdateAssignmentRequest request =
          CreateUpdateAssignmentRequest.builder()
              .assetId(1)
              .userId(2)
              .assignedDate(LocalDate.now().plusDays(1))
              .note("Test assignment")
              .build();

      AssignmentListDtoResponse serviceResponse =
          AssignmentListDtoResponse.builder()
              .id(1)
              .assetCode("LA000001")
              .assetName("Laptop Dell")
              .assignedTo("John Doe")
              .assignedBy("Admin User")
              .assignedDate(LocalDate.now().plusDays(1))
              .status("WAITING")
              .build();

      when(assignmentService.createAssignment(any(CreateUpdateAssignmentRequest.class)))
          .thenReturn(serviceResponse);

      ResponseEntity<ApiDtoResponse<AssignmentListDtoResponse>> response =
          assignmentController.createAssignment(request);

      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Assignment created successfully.", response.getBody().getMessage());
      assertEquals("LA000001", response.getBody().getData().getAssetCode());
    }
  }

  @Nested
  @DisplayName("editAssignment Tests")
  class EditAssignmentTest {

    @Test
    @DisplayName("should edit assignment successfully")
    void editAssignment_Success_ReturnsOk() {
      CreateUpdateAssignmentRequest request =
          CreateUpdateAssignmentRequest.builder()
              .assetId(2)
              .userId(3)
              .assignedDate(LocalDate.now().plusDays(2))
              .note("Updated assignment")
              .build();

      AssignmentListDtoResponse serviceResponse =
          AssignmentListDtoResponse.builder()
              .id(1)
              .assetCode("MO000001")
              .assetName("Monitor LG")
              .assignedTo("Jane Doe")
              .assignedBy("Admin User")
              .assignedDate(LocalDate.now().plusDays(2))
              .status("WAITING")
              .build();

      when(assignmentService.editAssignment(eq(1), any(CreateUpdateAssignmentRequest.class)))
          .thenReturn(serviceResponse);

      ResponseEntity<ApiDtoResponse<AssignmentListDtoResponse>> response =
          assignmentController.editAssignment(1, request);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Assignment updated successfully.", response.getBody().getMessage());
      assertEquals("MO000001", response.getBody().getData().getAssetCode());
    }
  }

  @Nested
  @DisplayName("getAssignmentDetails Tests")
  class GetAssignmentDetailsTest {

    @Test
    @DisplayName("should return assignment details successfully")
    void getAssignmentDetails_Success_ReturnsDetails() {
      AssignmentDetailDtoResponse detailResponse = new AssignmentDetailDtoResponse();
      detailResponse.setAssetCode("LA000001");
      detailResponse.setAssetName("Laptop Dell");

      ApiDtoResponse<AssignmentDetailDtoResponse> apiResponse =
          ApiDtoResponse.<AssignmentDetailDtoResponse>builder()
              .message("Assignment details retrieved successfully")
              .data(detailResponse)
              .build();

      when(assignmentService.getAssignmentDetails(1)).thenReturn(apiResponse);

      ResponseEntity<ApiDtoResponse<AssignmentDetailDtoResponse>> response =
          assignmentController.getAssignmentDetails(1);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("LA000001", response.getBody().getData().getAssetCode());
    }
  }

  @Nested
  @DisplayName("deleteAssignment Tests")
  class DeleteAssignmentTest {

    @Test
    @DisplayName("should delete assignment successfully")
    void deleteAssignment_Success_ReturnsOk() {
      ApiDtoResponse<Void> apiResponse =
          ApiDtoResponse.<Void>builder().message("Assignment deleted successfully").build();

      when(assignmentService.deleteAssignment(1)).thenReturn(apiResponse);

      ResponseEntity<ApiDtoResponse<Void>> response = assignmentController.deleteAssignment(1);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Assignment deleted successfully", response.getBody().getMessage());
    }
  }

  @Nested
  @DisplayName("getMyAssignments Tests")
  class GetMyAssignmentsTest {

    @Test
    @DisplayName("should return my assignments successfully")
    void getMyAssignments_Success_ReturnsAssignments() {
      MyAssignmentDtoResponse myAssignment = new MyAssignmentDtoResponse();
      myAssignment.setAssetCode("LA000001");
      myAssignment.setAssetName("Laptop Dell");

      ApiDtoResponse<List<MyAssignmentDtoResponse>> apiResponse =
          ApiDtoResponse.<List<MyAssignmentDtoResponse>>builder()
              .message("My assignments retrieved successfully")
              .data(List.of(myAssignment))
              .build();

      when(assignmentService.getMyAssignments("assetCode", "asc")).thenReturn(apiResponse);

      ResponseEntity<ApiDtoResponse<List<MyAssignmentDtoResponse>>> response =
          assignmentController.getMyAssignments("assetCode", "asc");

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals(1, response.getBody().getData().size());
    }
  }

  @Nested
  @DisplayName("respondToAssignment Tests")
  class RespondToAssignmentTest {

    @Test
    @DisplayName("should respond to assignment successfully")
    void respondToAssignment_Success_ReturnsOk() {
      AssignmentStatusResponse statusResponse = new AssignmentStatusResponse();
      statusResponse.setStatus(AssignmentStatus.ACCEPTED);

      when(assignmentService.responseToAssignment(1, AssignmentStatus.ACCEPTED))
          .thenReturn(statusResponse);

      ResponseEntity<ApiDtoResponse<AssignmentStatusResponse>> response =
          assignmentController.respondToAssignment(1, AssignmentStatus.ACCEPTED);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Assignment updated successfully.", response.getBody().getMessage());
      assertEquals(AssignmentStatus.ACCEPTED, response.getBody().getData().getStatus());
    }
  }

  @Nested
  @DisplayName("getAssignmentDetailForEdit Tests")
  class GetAssignmentDetailForEditTest {

    @Test
    @DisplayName("should return assignment detail for edit successfully")
    void getAssignmentDetailForEdit_Success_ReturnsDetails() {
      AssignmentDetailForEditResponse editResponse = new AssignmentDetailForEditResponse();
      editResponse.setAssignedDate(LocalDate.now());
      editResponse.setNote("Test note");

      when(assignmentService.getAssignmentDetailForEdit(1)).thenReturn(editResponse);

      ResponseEntity<ApiDtoResponse<AssignmentDetailForEditResponse>> response =
          assignmentController.getAssignmentDetailForEdit(1);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Assignment retrieved successfully", response.getBody().getMessage());
      assertNotNull(response.getBody().getData());
    }
  }
}
