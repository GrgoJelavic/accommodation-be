package com.evoapartments.accommodationbe.repository.user;

import com.evoapartments.accommodationbe.domain.user.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    ApplicationUser findByEmailIgnoreCase(String email);
    Boolean existsByEmail(String email);
    void deleteByEmail(String email);
    Optional<ApplicationUser> findByEmail(String email);
}
