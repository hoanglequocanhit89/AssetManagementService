package com.rookie.asset_management.service.specification;

import com.rookie.asset_management.entity.Asset;
import com.rookie.asset_management.enums.AssetStatus;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specification class for filtering Asset entities. This class can be used to create dynamic
 * queries based on various asset attributes.
 */
public class AssetSpecification {
  private AssetSpecification() {
    // Private constructor to prevent instantiation
  }

  /**
   * Specification to filter assets by location ID.
   *
   * @param locationId the ID of the location to filter assets by
   * @return a Specification that filters assets by location ID
   */
  public static Specification<Asset> hasLocationId(Integer locationId) {
    return (root, query, cb) -> cb.equal(root.get("location").get("id"), locationId);
  }

  /**
   * Specification to filter assets by disabled status. This specification excludes assets that are
   * marked as disabled.
   *
   * @return a Specification that filters out disabled assets
   */
  public static Specification<Asset> excludeDisabled() {
    return (root, query, cb) -> cb.isFalse(root.get("disabled"));
  }

  /**
   * Specification to filter assets by name or asset code.
   *
   * @param query the search query to match against asset name or code
   * @return a Specification that filters assets by name or asset code
   */
  public static Specification<Asset> hasNameOrCodeLike(String query) {
    return (root, query1, cb) ->
        cb.or(
            cb.like(cb.lower(root.get("name")), "%" + query.toLowerCase() + "%"),
            cb.like(cb.lower(root.get("assetCode")), "%" + query.toLowerCase() + "%"));
  }

  /**
   * Specification to filter assets by category name.
   *
   * @param categoryName the name of the category to filter assets by
   * @return a Specification that filters assets by category name
   */
  public static Specification<Asset> hasCategoryName(String categoryName) {
    return (root, query, cb) ->
        cb.like(cb.lower(root.get("category").get("name")), "%" + categoryName.toLowerCase() + "%");
  }

  /**
   * Specification to filter assets by state (status). - can be used to filter by multiple states.
   *
   * @param states the list of states to filter assets by
   * @return a Specification that filters assets by their state
   */
  public static Specification<Asset> hasStateIn(List<AssetStatus> states) {
    return (root, query, cb) -> root.get("status").in(states);
  }

  /**
   * Specification to filter assets that do not have any assignments with a specific status.
   *
   * @param status the status of the assignment to filter assets by
   * @return a Specification that filters assets without assignments of the given status
   */
  public static Specification<Asset> excludeAssignmentStatus(AssetStatus status) {
    return (root, query, cb) -> {
      var subquery = query.subquery(Integer.class);
      var subRoot = subquery.from(Asset.class);
      subquery
          .select(cb.literal(1))
          .where(
              cb.equal(root, subRoot),
              cb.equal(subRoot.join("assignments").get("status"), status),
              cb.equal(subRoot.join("assignments").get("deleted"), false));
      return cb.not(cb.exists(subquery));
    };
  }
}
