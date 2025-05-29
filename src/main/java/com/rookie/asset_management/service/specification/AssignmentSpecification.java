package com.rookie.asset_management.service.specification;

import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.Location;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.enums.AssignmentStatus;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/** Specification class for filtering Assignment entities. */
public class AssignmentSpecification {

  private AssignmentSpecification() {
    // Private constructor to prevent instantiation
  }

  /**
   * Specification to filter assignments by status.
   *
   * @param status the status to filter by
   * @return a Specification that filters assignments by status
   */
  public static Specification<Assignment> hasStatus(AssignmentStatus status) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
  }

  /**
   * Specification to filter assignments by assigned date.
   *
   * @param assignedDate the assigned date in format yyyy-MM-dd
   * @return a Specification that filters assignments by assigned date
   */
  public static Specification<Assignment> hasAssignedDate(String assignedDate) {
    return (root, query, criteriaBuilder) -> {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDate date = LocalDate.parse(assignedDate, formatter);
      return criteriaBuilder.equal(root.get("assignedDate"), date);
    };
  }

  /**
   * Specification to filter assignments by asset name, asset code, or assignee's name.
   *
   * @param query the search query
   * @return a Specification that filters assignments by the search query
   */
  public static Specification<Assignment> hasAssetOrAssigneeLike(String query) {
    return (root, query1, criteriaBuilder) ->
        criteriaBuilder.or(
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("asset").get("name")),
                "%" + query.toLowerCase() + "%"),
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("asset").get("assetCode")),
                "%" + query.toLowerCase() + "%"),
            criteriaBuilder.like(
                criteriaBuilder.lower(
                    criteriaBuilder.concat(
                        criteriaBuilder.concat(
                            root.get("assignedTo").get("username"), criteriaBuilder.literal(" ")),
                        root.get("assignedTo").get("username"))),
                "%" + query.toLowerCase() + "%"));
  }

  /**
   * Specification to filter assignments by admin's location.
   *
   * @param adminId the ID of the admin user
   * @return a Specification that filters assignments by admin's location
   */
  public static Specification<Assignment> hasSameLocationAs(Integer adminId) {
    return (root, query, criteriaBuilder) -> {
      Subquery<Location> locationSubquery = query.subquery(Location.class);
      Root<User> admin = locationSubquery.from(User.class);
      locationSubquery
          .select(admin.get("location"))
          .where(criteriaBuilder.equal(admin.get("id"), adminId));
      return criteriaBuilder.and(
          criteriaBuilder.equal(root.get("asset").get("location"), locationSubquery),
          criteriaBuilder.equal(root.get("assignedTo").get("location"), locationSubquery));
    };
  }

  /**
   * Specification to exclude deleted assignments.
   *
   * @return a Specification that excludes deleted assignments
   */
  public static Specification<Assignment> excludeDeleted() {
    return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("deleted"));
  }

  /**
   * Returns a JPA Specification to filter {@link Assignment} entities by the ID of the user they
   * are assigned to.
   *
   * @param userId the ID of the user to whom the assignment is assigned
   * @return a {@link Specification} that filters assignments based on the assigned user's ID
   */
  public static Specification<Assignment> hasAssignedTo(Integer userId) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("assignedTo").get("id"), userId);
  }

  /**
   * Returns a JPA Specification to filter {@link Assignment} entities that have a status included
   * in the given list of statuses.
   *
   * @param statuses a list of {@link AssignmentStatus} values to filter by
   * @return a {@link Specification} that filters assignments whose status is in the provided list
   */
  public static Specification<Assignment> hasStatusIn(List<AssignmentStatus> statuses) {
    return (root, query, criteriaBuilder) -> root.get("status").in(statuses);
  }

  /** Specification to order assignments by status alphabetically */
  public static Specification<Assignment> orderByStatus(String sortDir) {
    return (root, query, cb) -> {
      query.orderBy(
          sortDir.equalsIgnoreCase("desc")
              ? cb.desc(
                  cb.selectCase(root.get("status"))
                      .when(AssignmentStatus.WAITING, 1)
                      .when(AssignmentStatus.DECLINED, 2)
                      .when(AssignmentStatus.ACCEPTED, 3)
                      .otherwise(4))
              : cb.asc(
                  cb.selectCase(root.get("status"))
                      .when(AssignmentStatus.WAITING, 1)
                      .when(AssignmentStatus.DECLINED, 2)
                      .when(AssignmentStatus.ACCEPTED, 3)
                      .otherwise(4)));
      return null;
    };
  }

  /**
   * Specification to filter assignments with assigned date less than or equal to the current date.
   *
   * @return a Specification that filters assignments by assigned date <= current date
   */
  public static Specification<Assignment> hasAssignedDateBeforeOrEqualToCurrent() {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.lessThanOrEqualTo(root.get("assignedDate"), LocalDate.now());
  }
}
