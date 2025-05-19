package com.rookie.asset_management.repository;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * SpecificationRepository interface for all entities. This interface extends JpaRepository and
 * JpaSpecificationExecutor to provide basic CRUD operations and specification-based querying
 * capabilities.
 *
 * @param <E> the entity type
 * @param <K> the type of the entity's identifier. This is a {@link Serializable} type.
 */
@NoRepositoryBean
// Indicates that this is not a repository bean and should not be instantiated directly
public interface SpecificationRepository<E, K extends Serializable>
    extends JpaRepository<E, K>, JpaSpecificationExecutor<E> {}
