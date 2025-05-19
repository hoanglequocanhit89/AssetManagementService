package com.rookie.asset_management.service.specification;

import com.rookie.asset_management.entity.Location;
import com.rookie.asset_management.entity.User;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specification class for filtering User entities. This class contains static methods to create
 * specifications based on different criteria.
 */
public class UserSpecification {

  private UserSpecification() {
    // Private constructor to prevent instantiation
  }

  /**
   * Specification to filter users by location.
   *
   * @param adminId the ID of the admin user
   * @return a Specification that filters users by location
   */
  public static Specification<User> hasSameLocationAs(Integer adminId) {
    return (root, query, cb) -> {
      Subquery<Location> locationSubquery = query.subquery(Location.class);
      Root<User> admin = locationSubquery.from(User.class);
      locationSubquery.select(admin.get("location")).where(cb.equal(admin.get("id"), adminId));
      return cb.equal(root.get("location"), locationSubquery);
    };
  }

  /**
   * Specification to exclude a specific admin user from the results.
   *
   * @param adminId the ID of the admin user to exclude
   * @return a Specification that excludes the specified admin user
   */
  public static Specification<User> excludeAdmin(Integer adminId) {
    return (root, query, cb) -> cb.notEqual(root.get("id"), adminId);
  }

  /**
   * Specification to filter users by first name.
   *
   * @param name the first name to filter by
   * @return a Specification that filters users by first name
   */
  public static Specification<User> hasFirstNameOrLastNameLike(String name) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.or(
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("userProfile").get("firstName")),
                "%" + name.toLowerCase() + "%"),
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("userProfile").get("lastName")),
                "%" + name.toLowerCase() + "%"));
  }

  /**
   * Specification to filter users by staff code.
   *
   * @param staffCode the staff code to filter by
   * @return a Specification that filters users by staff code
   */
  public static Specification<User> hasStaffCodeLike(String staffCode) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.like(
            criteriaBuilder.lower(root.get("staffCode")), "%" + staffCode.toLowerCase() + "%");
  }

  /**
   * Specification to filter users by type
   *
   * @param type the type to filter by
   * @return a Specification that filters users by type
   */
  public static Specification<User> hasType(String type) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("role").get("name"), type);
  }
}
