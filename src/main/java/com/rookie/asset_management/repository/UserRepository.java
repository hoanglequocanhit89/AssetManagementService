package com.rookie.asset_management.repository;

import com.rookie.asset_management.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByIdAndDisabledFalse(Integer id);
}
