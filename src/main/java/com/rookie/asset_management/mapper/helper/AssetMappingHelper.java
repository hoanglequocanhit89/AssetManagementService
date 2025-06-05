package com.rookie.asset_management.mapper.helper;

import com.rookie.asset_management.dto.response.assignment.AssignmentDtoResponse;
import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.Category;
import com.rookie.asset_management.entity.ReturningRequest;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.repository.AssetRepository;
import com.rookie.asset_management.repository.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mapstruct.Named;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * AssetMappingHelper is a utility class that provides helper methods for mapping asset-related
 * operations. It is designed to assist in the mapping and retrieval of asset data from the
 * AssetRepository.
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class AssetMappingHelper {
  AssetRepository assetRepository;
  CategoryRepository categoryRepository;

  /**
   * Checks if an asset with the given ID can be deleted. An asset can be deleted if it does not
   * have any assignments associated with it.
   *
   * @param assetId the ID of the asset to check
   * @return true if the asset cannot be deleted (i.e., it has assignments), false otherwise
   */
  @Named("mapCanNotDeleteAsset")
  public boolean canNotDeleteAsset(Integer assetId) {
    return !assetRepository.existsAssignmentByAssetId(assetId);
  }

  /**
   * Maps a category ID to a Category entity. This method retrieves the Category entity from the
   * CategoryRepository based on the provided ID. If the category is not found, it throws an
   * IllegalArgumentException.
   *
   * @param id the ID of the category to retrieve
   * @return the Category entity corresponding to the given ID
   */
  @Named("mapToCategoryById")
  public Category mapToCategoryById(Integer id) {
    return categoryRepository
        .findById(id)
        .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Category not found"));
  }

  /**
   * Maps a list of Assignment entities to a list of AssignmentDtoResponse DTOs. This method
   * converts each Assignment entity into an AssignmentDtoResponse, extracting relevant fields such
   * as assignedTo, assignedBy, assignedDate, and returnedDate. If the list of assignments is null
   * or empty, it returns an empty list.
   *
   * @param assignments the list of Assignment entities to map
   * @return a list of AssignmentDtoResponse DTOs representing the assignment history
   */
  @Named("mapToAssignmentHistory")
  public List<AssignmentDtoResponse> mapToAssignmentHistory(List<Assignment> assignments) {
    if (assignments == null || assignments.isEmpty()) {
      return List.of();
    }
    return assignments.stream()
        .map(
            assignment -> {
              AssignmentDtoResponse dto = new AssignmentDtoResponse();
              dto.setAssignedTo(assignment.getAssignedTo().getUsername());
              dto.setAssignedBy(assignment.getAssignedBy().getUsername());
              dto.setAssignedDate(assignment.getAssignedDate());
              // Check if the assignment has a returning request and set the returned date if
              // available
              ReturningRequest returningRequest = assignment.getReturningRequest();
              if (returningRequest != null && returningRequest.getReturnedDate() != null) {
                dto.setReturnedDate(returningRequest.getReturnedDate());
              }
              return dto;
            })
        .toList();
  }
}
