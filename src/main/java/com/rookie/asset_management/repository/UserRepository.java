package com.rookie.asset_management.repository;

import com.rookie.asset_management.entity.User;

/**
 * Repository interface for User entity. It extends the SpecificationRepository interface to provide
 * basic CRUD operations and custom query capabilities.
 */
public interface UserRepository extends SpecificationRepository<User, Integer> {}
