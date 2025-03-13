package com.trego.dao.impl;

import com.trego.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query methods can be added here if needed, e.g., find by email
    Optional<User> findByEmail(String email);
}
