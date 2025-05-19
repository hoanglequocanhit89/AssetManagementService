package com.rookie.asset_management.service.specification;

import com.rookie.asset_management.entity.User;
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
   * Specification to filter users by first name.
   *
   * @param name the first name to filter by
   * @return a Specification that filters users by first name
   */
  public static Specification<User> hasName(String name) {
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
  public static Specification<User> hasStaffCode(String staffCode) {
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
