package com.rookie.asset_management.service.specification;

import org.springframework.data.jpa.domain.Specification;

/**
 * A generic class to build JPA specifications dynamically.
 *
 * @param <T> the type of the entity
 */
public class SpecificationBuilder<T> {
  /**
   * The current specification.
   * This is initialized to null and will be built up using the addIfNotNull and add method.
   */
  private Specification<T> spec = Specification.where(null);

  /**
   * Adds a specification to the current specification.
   * This method is used to chain multiple specifications together.
   *
   * @param specFunc the function that generates the specification
   * @return the current SpecificationBuilder instance
   */
  public SpecificationBuilder<T> add(Specification<T> specFunc) {
    if (specFunc != null) {
      spec = spec.and(specFunc);
    }
    return this;
  }

  /**
   * Builds the final specification.
   * This method returns the current specification, which can be used in a query.
   *
   * @return the final specification
   */
  public Specification<T> build() {
    return spec;
  }
}
