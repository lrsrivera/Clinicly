package com.icc.clinic.repository;

import com.icc.clinic.model.Role;
import com.icc.clinic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByActive(Boolean active);
    Optional<User> findByEmail(String email);
} 