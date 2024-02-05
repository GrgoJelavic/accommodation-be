package com.evoapartments.accommodationbe.service.user;

import com.evoapartments.accommodationbe.domain.user.Role;
import com.evoapartments.accommodationbe.domain.user.ApplicationUser;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface IRoleService {

    List<Role> getRoles();

    Role findRoleByName(String name);

    Role createRole(Role role);

    Role updateUserRole(Long roleId, String name);

    void deleteRole(Long roleId);

    ApplicationUser removeRoleFromUser(Long userId, Long roleId);

    ApplicationUser assignRoleToUser(Long userId, Long roleId);

    Role removeRoleFromAllUsers(Long roleId);
}
