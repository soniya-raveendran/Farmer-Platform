package com.farmer.repository;

import com.farmer.entity.Role;
import com.farmer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRole(Role role);

    java.util.List<User> findByRole(Role role);
}
