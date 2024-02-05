package com.evoapartments.accommodationbe.repository;


import com.evoapartments.accommodationbe.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

   Optional<Role> findRoleByName(String roleName);

    boolean existsByName(String roleName);
}
