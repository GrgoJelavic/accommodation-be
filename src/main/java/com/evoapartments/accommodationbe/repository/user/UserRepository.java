package com.evoapartments.accommodationbe.repository;

import com.evoapartments.accommodationbe.model.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    boolean existsByEmail(String email);

    void deleteByEmail(String email);

    Optional<ApplicationUser> findByEmail(String email);
}
