package com.rookie.asset_management.service.specification;

import com.rookie.asset_management.entity.Location;
import com.rookie.asset_management.entity.ReturningRequest;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.enums.ReturningRequestStatus;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.data.jpa.domain.Specification;

/** Specification class for filtering ReturningRequest entities. */
public class ReturningRequestSpecification {

  private ReturningRequestSpecification() {
    // Private constructor to prevent instantiation
  }

  /**
   * Creates a specification to filter ReturningRequest entities by their status.
   *
   * @param status the status to filter by
   * @return a Specification for filtering ReturningRequest entities
   */
  public static Specification<ReturningRequest> hasStatus(ReturningRequestStatus status) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
  }

  /**
   * Creates a specification to filter ReturningRequest entities by their returned date.
   *
   * @param returnedDate the returned date to filter by, in the format "yyyy-MM-dd"
   * @return a Specification for filtering ReturningRequest entities by returned date
   * @throws java.time.format.DateTimeParseException if the provided date string is not in the
   *     expected format
   */
  public static Specification<ReturningRequest> hasReturnedDate(String returnedDate) {
    return (root, query, criteriaBuilder) -> {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDate date = LocalDate.parse(returnedDate, formatter);
      return criteriaBuilder.equal(root.get("returnedDate"), date);
    };
  }

  /**
   * Creates a specification to filter ReturningRequest entities that have the same location as the
   * admin with the specified ID. This method uses a subquery to determine the location of the admin
   * and compares it with the location of the asset associated with the ReturningRequest.
   *
   * @param adminId the ID of the admin whose location is used for filtering
   * @return a Specification for filtering ReturningRequest entities by location
   */
  public static Specification<ReturningRequest> hasSameLocationAs(Integer adminId) {
    return (root, query, criteriaBuilder) -> {
      Subquery<Location> locationSubquery = query.subquery(Location.class);
      Root<User> admin = locationSubquery.from(User.class);
      locationSubquery
          .select(admin.get("location"))
          .where(criteriaBuilder.equal(admin.get("id"), adminId));
      return criteriaBuilder.and(
          criteriaBuilder.equal(
              root.get("assignment").get("asset").get("location"), locationSubquery));
    };
  }

  /**
   * Creates a specification to filter ReturningRequest entities by asset name, asset code, or the
   * username of the requester. The search is case-insensitive and supports partial matches.
   *
   * @param query the search query to match against asset name, asset code, or requester username
   * @return a Specification for filtering ReturningRequest entities
   */
  public static Specification<ReturningRequest> hasAssetOrRequesterLike(String query) {
    return (root, query1, criteriaBuilder) ->
        criteriaBuilder.or(
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("assignment").get("asset").get("name")),
                "%" + query.toLowerCase() + "%"),
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("assignment").get("asset").get("assetCode")),
                "%" + query.toLowerCase() + "%"),
            criteriaBuilder.like(
                criteriaBuilder.lower(
                    criteriaBuilder.concat(
                        criteriaBuilder.concat(
                            root.get("requestedBy").get("username"), criteriaBuilder.literal(" ")),
                        root.get("requestedBy").get("username"))),
                "%" + query.toLowerCase() + "%"));
  }

  /**
   * Creates a specification to order ReturningRequest entities by their status. The ordering is
   * based on a custom priority: WAITING (1), COMPLETED (2), and others (3).
   *
   * @param sortDir the direction of sorting, either "asc" for ascending or "desc" for descending
   * @return a Specification for ordering ReturningRequest entities by status
   */
  public static Specification<ReturningRequest> orderByStatus(String sortDir) {
    return (root, query, cb) -> {
      query.orderBy(
          sortDir.equalsIgnoreCase("desc")
              ? cb.desc(
                  cb.selectCase(root.get("status"))
                      .when(ReturningRequestStatus.WAITING, 1)
                      .when(ReturningRequestStatus.COMPLETED, 2)
                      .otherwise(3))
              : cb.asc(
                  cb.selectCase(root.get("status"))
                      .when(ReturningRequestStatus.WAITING, 1)
                      .when(ReturningRequestStatus.COMPLETED, 2)
                      .otherwise(3)));
      return null;
    };
  }

  /**
   * Creates a specification to exclude ReturningRequest entities that were requested by the admin
   * with the specified ID.
   *
   * @param adminId the ID of the admin whose requests should be excluded
   * @return a Specification for excluding ReturningRequest entities requested by the admin
   */
  public static Specification<ReturningRequest> excludeAdminRequests(Integer adminId) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.notEqual(root.get("requestedBy").get("id"), adminId);
  }
}
