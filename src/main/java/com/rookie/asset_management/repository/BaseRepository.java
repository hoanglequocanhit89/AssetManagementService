package com.rookie.asset_management.repository;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Base repository interface for all entities. This interface extends JpaRepository to provide basic
 * CRUD operations.
 *
 * @param <T> the entity type
 * @param <K> the type of the entity's identifier. This is a {@link Serializable} type
 */
@NoRepositoryBean
// Indicates that this is not a repository bean and should not be instantiated directly
public interface BaseRepository<T, K extends Serializable> extends JpaRepository<T, K> {}
