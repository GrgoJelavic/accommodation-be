package com.evoapartments.accommodationbe.service;

import com.evoapartments.accommodationbe.model.user.Role;
import com.evoapartments.accommodationbe.model.user.ApplicationUser;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface IRoleService {
    List<Role> getRoles();

    Role createRole(Role role);

    void deleteRole(Long roleId);

    Role findRoleByName(String name);

    ApplicationUser removeRoleFromUser(Long userId, Long roleId);

    ApplicationUser assignRoleToUser(Long userId, Long roleId);

    Role removeRoleFromAllUsers(Long roleId);
}
