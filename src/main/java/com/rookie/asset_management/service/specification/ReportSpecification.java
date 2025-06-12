package com.rookie.asset_management.service.specification;

import com.rookie.asset_management.entity.Category;
import com.rookie.asset_management.enums.AssetStatus;
import jakarta.persistence.criteria.*;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.jpa.domain.Specification;

/** Specification class for filtering Report entities. */
public class ReportSpecification {

  private static final Map<String, AssetStatus> STATUS_MAP = new HashMap<>();

  static {
    STATUS_MAP.put("assigned", AssetStatus.ASSIGNED);
    STATUS_MAP.put("available", AssetStatus.AVAILABLE);
    STATUS_MAP.put("notAvailable", AssetStatus.NOT_AVAILABLE);
    STATUS_MAP.put("waiting", AssetStatus.WAITING);
    STATUS_MAP.put("recycled", AssetStatus.RECYCLED);
  }

  private ReportSpecification() {
    // Private constructor to prevent instantiation
  }

  /**
   * Specification to filter categories by assets count and sort them.
   *
   * @param sortBy the field to sort by, e.g., {@code "total"}, {@code "assigned"}, {@code
   *     "available"}, etc.
   * @param sortDir the direction of sorting ({@code "asc"}, {@code "desc"})
   * @return a Specification that filters categories by assets count and sorts them
   */
  public static Specification<Category> getSortedByAssetsCount(String sortBy, String sortDir) {
    return (root, query, cb) -> {
      // Join with assets
      Join<Object, Object> assetJoin = root.join("assets", JoinType.LEFT);

      // For count queries, return early
      assert query != null;
      if (query.getResultType().equals(Long.class)) {
        query.distinct(true); // for count queries, ensure distinct categories
        return cb.conjunction();
      }

      // Group by category
      query.groupBy(root.get("id"), root.get("name"), root.get("prefix"));

      // Apply sorting based on parameters
      applySorting(sortBy, sortDir, query, cb, assetJoin, root);

      return cb.conjunction();
    };
  }

  private static void applySorting(
      String sortBy,
      String sortDir,
      CriteriaQuery<?> query,
      CriteriaBuilder cb,
      Join<Object, Object> assetJoin,
      Root<Category> root) {

    if (sortBy == null || sortBy.isEmpty()) {
      // Default sorting by category ID if no sortBy is provided
      query.orderBy(cb.asc(root.get("id")));
      return;
    }

    if (sortDir == null || sortDir.isEmpty()) {
      // Default sorting direction is ascending if not provided
      sortDir = "asc";
    }

    boolean isDesc = sortDir.equalsIgnoreCase("desc");

    // Handle total count case
    if (sortBy.equalsIgnoreCase("total")) {
      Expression<Long> totalCount = cb.count(assetJoin);
      // Select the category and the total count of assets
      query.multiselect(root, totalCount);
      query.orderBy(isDesc ? cb.desc(totalCount) : cb.asc(totalCount));
      return;
    }

    // Get the status for the given sortBy parameter
    AssetStatus status = STATUS_MAP.get(sortBy.toLowerCase());

    if (status != null) {
      // Apply sorting based on status count
      Expression<Long> statusCount =
          cb.sum(
              cb.<Long>selectCase()
                  .when(cb.equal(assetJoin.get("status"), status), 1L)
                  .otherwise(0L));
      // Select the category and the status count
      query.multiselect(root, statusCount);
      query.orderBy(isDesc ? cb.desc(statusCount) : cb.asc(statusCount));
    }
    // If no matching status found, fall back to sorting by category ID
    else {
      query.orderBy(isDesc ? cb.desc(root.get("id")) : cb.asc(root.get("id")));
    }
  }
}
