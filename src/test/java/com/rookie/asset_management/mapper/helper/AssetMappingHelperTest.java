package com.rookie.asset_management.mapper.helper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rookie.asset_management.dto.response.assignment.AssignmentDtoResponse;
import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.Category;
import com.rookie.asset_management.entity.ReturningRequest;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.enums.AssignmentStatus;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.repository.AssetRepository;
import com.rookie.asset_management.repository.CategoryRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class AssetMappingHelperTest {

  @Mock private AssetRepository assetRepository;

  @Mock private CategoryRepository categoryRepository;

  @InjectMocks private AssetMappingHelper assetMappingHelper;

  @Nested
  @DisplayName("canNotDeleteAsset Tests")
  class CanNotDeleteAssetTest {

    @Test
    @DisplayName("canNotDeleteAsset should return true when asset has no assignments")
    void canNotDeleteAsset_NoAssignments_ReturnsTrue() {
      when(assetRepository.existsAssignmentByAssetId(1)).thenReturn(false);

      boolean result = assetMappingHelper.canNotDeleteAsset(1);

      assertTrue(result);
      verify(assetRepository).existsAssignmentByAssetId(1);
    }

    @Test
    @DisplayName("canNotDeleteAsset should return false when asset has assignments")
    void canNotDeleteAsset_HasAssignments_ReturnsFalse() {
      when(assetRepository.existsAssignmentByAssetId(1)).thenReturn(true);

      boolean result = assetMappingHelper.canNotDeleteAsset(1);

      assertFalse(result);
      verify(assetRepository).existsAssignmentByAssetId(1);
    }
  }

  @Nested
  @DisplayName("mapToCategoryById Tests")
  class MapToCategoryByIdTest {

    @Test
    @DisplayName("mapToCategoryById should return category when found")
    void mapToCategoryById_CategoryExists_ReturnsCategory() {
      Category category = new Category();
      category.setId(1);
      category.setName("Laptop");
      category.setPrefix("LA");

      when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

      Category result = assetMappingHelper.mapToCategoryById(1);

      assertNotNull(result);
      assertEquals(1, result.getId());
      assertEquals("Laptop", result.getName());
      verify(categoryRepository).findById(1);
    }

    @Test
    @DisplayName("mapToCategoryById should throw AppException when category not found")
    void mapToCategoryById_CategoryNotFound_ThrowsAppException() {
      when(categoryRepository.findById(999)).thenReturn(Optional.empty());

      AppException exception =
          assertThrows(AppException.class, () -> assetMappingHelper.mapToCategoryById(999));

      assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatusCode());
      assertTrue(exception.getMessage().contains("Category not found"));
      verify(categoryRepository).findById(999);
    }
  }

  @Nested
  @DisplayName("mapToAssignmentHistory Tests")
  class MapToAssignmentHistoryTest {

    private User assignedTo;
    private User assignedBy;

    @BeforeEach
    void setUp() {
      assignedTo = new User();
      assignedTo.setId(1);
      assignedTo.setUsername("user1");

      assignedBy = new User();
      assignedBy.setId(2);
      assignedBy.setUsername("admin1");
    }

    @Test
    @DisplayName("mapToAssignmentHistory should return empty list when assignments is null")
    void mapToAssignmentHistory_NullAssignments_ReturnsEmptyList() {
      List<AssignmentDtoResponse> result = assetMappingHelper.mapToAssignmentHistory(null);

      assertNotNull(result);
      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("mapToAssignmentHistory should return empty list when assignments is empty")
    void mapToAssignmentHistory_EmptyAssignments_ReturnsEmptyList() {
      List<AssignmentDtoResponse> result =
          assetMappingHelper.mapToAssignmentHistory(new ArrayList<>());

      assertNotNull(result);
      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("mapToAssignmentHistory should filter out deleted assignments")
    void mapToAssignmentHistory_FilterDeletedAssignments() {
      Assignment deletedAssignment = new Assignment();
      deletedAssignment.setId(1);
      deletedAssignment.setDeleted(true);
      deletedAssignment.setStatus(AssignmentStatus.ACCEPTED);
      deletedAssignment.setAssignedTo(assignedTo);
      deletedAssignment.setAssignedBy(assignedBy);
      deletedAssignment.setAssignedDate(LocalDate.now());

      List<Assignment> assignments = List.of(deletedAssignment);

      List<AssignmentDtoResponse> result = assetMappingHelper.mapToAssignmentHistory(assignments);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("mapToAssignmentHistory should filter out DECLINED assignments")
    void mapToAssignmentHistory_FilterDeclinedAssignments() {
      Assignment declinedAssignment = new Assignment();
      declinedAssignment.setId(1);
      declinedAssignment.setDeleted(false);
      declinedAssignment.setStatus(AssignmentStatus.DECLINED);
      declinedAssignment.setAssignedTo(assignedTo);
      declinedAssignment.setAssignedBy(assignedBy);
      declinedAssignment.setAssignedDate(LocalDate.now());

      List<Assignment> assignments = List.of(declinedAssignment);

      List<AssignmentDtoResponse> result = assetMappingHelper.mapToAssignmentHistory(assignments);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("mapToAssignmentHistory should filter out WAITING assignments")
    void mapToAssignmentHistory_FilterWaitingAssignments() {
      Assignment waitingAssignment = new Assignment();
      waitingAssignment.setId(1);
      waitingAssignment.setDeleted(false);
      waitingAssignment.setStatus(AssignmentStatus.WAITING);
      waitingAssignment.setAssignedTo(assignedTo);
      waitingAssignment.setAssignedBy(assignedBy);
      waitingAssignment.setAssignedDate(LocalDate.now());

      List<Assignment> assignments = List.of(waitingAssignment);

      List<AssignmentDtoResponse> result = assetMappingHelper.mapToAssignmentHistory(assignments);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("mapToAssignmentHistory should map ACCEPTED assignments correctly")
    void mapToAssignmentHistory_AcceptedAssignment_MapsCorrectly() {
      Assignment acceptedAssignment = new Assignment();
      acceptedAssignment.setId(1);
      acceptedAssignment.setDeleted(false);
      acceptedAssignment.setStatus(AssignmentStatus.ACCEPTED);
      acceptedAssignment.setAssignedTo(assignedTo);
      acceptedAssignment.setAssignedBy(assignedBy);
      acceptedAssignment.setAssignedDate(LocalDate.of(2024, 1, 15));

      List<Assignment> assignments = List.of(acceptedAssignment);

      List<AssignmentDtoResponse> result = assetMappingHelper.mapToAssignmentHistory(assignments);

      assertEquals(1, result.size());
      AssignmentDtoResponse dto = result.get(0);
      assertEquals("user1", dto.getAssignedTo());
      assertEquals("admin1", dto.getAssignedBy());
      assertEquals(LocalDate.of(2024, 1, 15), dto.getAssignedDate());
      assertNull(dto.getReturnedDate());
    }

    @Test
    @DisplayName("mapToAssignmentHistory should include returnedDate when returning request exists")
    void mapToAssignmentHistory_WithReturningRequest_IncludesReturnedDate() {
      ReturningRequest returningRequest = new ReturningRequest();
      returningRequest.setReturnedDate(LocalDate.of(2024, 2, 20));

      Assignment assignment = new Assignment();
      assignment.setId(1);
      assignment.setDeleted(false);
      assignment.setStatus(AssignmentStatus.ACCEPTED);
      assignment.setAssignedTo(assignedTo);
      assignment.setAssignedBy(assignedBy);
      assignment.setAssignedDate(LocalDate.of(2024, 1, 15));
      assignment.setReturningRequest(returningRequest);

      List<Assignment> assignments = List.of(assignment);

      List<AssignmentDtoResponse> result = assetMappingHelper.mapToAssignmentHistory(assignments);

      assertEquals(1, result.size());
      AssignmentDtoResponse dto = result.get(0);
      assertEquals(LocalDate.of(2024, 2, 20), dto.getReturnedDate());
    }

    @Test
    @DisplayName(
        "mapToAssignmentHistory should not include returnedDate when returning request has null date")
    void mapToAssignmentHistory_ReturningRequestNullDate_NoReturnedDate() {
      ReturningRequest returningRequest = new ReturningRequest();
      returningRequest.setReturnedDate(null);

      Assignment assignment = new Assignment();
      assignment.setId(1);
      assignment.setDeleted(false);
      assignment.setStatus(AssignmentStatus.ACCEPTED);
      assignment.setAssignedTo(assignedTo);
      assignment.setAssignedBy(assignedBy);
      assignment.setAssignedDate(LocalDate.of(2024, 1, 15));
      assignment.setReturningRequest(returningRequest);

      List<Assignment> assignments = List.of(assignment);

      List<AssignmentDtoResponse> result = assetMappingHelper.mapToAssignmentHistory(assignments);

      assertEquals(1, result.size());
      assertNull(result.get(0).getReturnedDate());
    }

    @Test
    @DisplayName("mapToAssignmentHistory should filter and map multiple assignments correctly")
    void mapToAssignmentHistory_MultipleAssignments_FiltersAndMapsCorrectly() {
      Assignment acceptedAssignment = new Assignment();
      acceptedAssignment.setId(1);
      acceptedAssignment.setDeleted(false);
      acceptedAssignment.setStatus(AssignmentStatus.ACCEPTED);
      acceptedAssignment.setAssignedTo(assignedTo);
      acceptedAssignment.setAssignedBy(assignedBy);
      acceptedAssignment.setAssignedDate(LocalDate.of(2024, 1, 15));

      Assignment deletedAssignment = new Assignment();
      deletedAssignment.setId(2);
      deletedAssignment.setDeleted(true);
      deletedAssignment.setStatus(AssignmentStatus.ACCEPTED);
      deletedAssignment.setAssignedTo(assignedTo);
      deletedAssignment.setAssignedBy(assignedBy);
      deletedAssignment.setAssignedDate(LocalDate.now());

      Assignment waitingAssignment = new Assignment();
      waitingAssignment.setId(3);
      waitingAssignment.setDeleted(false);
      waitingAssignment.setStatus(AssignmentStatus.WAITING);
      waitingAssignment.setAssignedTo(assignedTo);
      waitingAssignment.setAssignedBy(assignedBy);
      waitingAssignment.setAssignedDate(LocalDate.now());

      List<Assignment> assignments =
          List.of(acceptedAssignment, deletedAssignment, waitingAssignment);

      List<AssignmentDtoResponse> result = assetMappingHelper.mapToAssignmentHistory(assignments);

      assertEquals(1, result.size());
      assertEquals("user1", result.get(0).getAssignedTo());
    }
  }
}
