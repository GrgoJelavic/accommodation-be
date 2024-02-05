package com.evoapartments.accommodationbe.service.user.impl;

import com.evoapartments.accommodationbe.exception.ResourceNotFoundException;
import com.evoapartments.accommodationbe.exception.RoleAlreadyExistsException;
import com.evoapartments.accommodationbe.exception.UserAlreadyExistsException;
import com.evoapartments.accommodationbe.domain.user.Role;
import com.evoapartments.accommodationbe.domain.user.ApplicationUser;
import com.evoapartments.accommodationbe.repository.user.RoleRepository;
import com.evoapartments.accommodationbe.repository.user.UserRepository;
import com.evoapartments.accommodationbe.service.user.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role findRoleByName(String roleName) {
        return roleRepository.findRoleByName(roleName).get();
    }

    @Override
    public Role createRole(Role role) {
        String roleName = "ROLE_" + role.getName().toUpperCase();
        Role newRole = new Role(roleName);
        if (roleRepository.existsByName(roleName)) {
            throw new RoleAlreadyExistsException(role.getName() + " role already exists");
        }
        return roleRepository.save(newRole);
    }

    @Override
    public Role updateUserRole(Long roleId, String name) {
        Role userRole = roleRepository.findById(roleId).orElseThrow(
                () -> new ResourceNotFoundException("User role not found!"));
        if (name != null) {
            userRole.setName(name);
        }
        return roleRepository.save(userRole);
    }

    @Override
    public void deleteRole(Long roleId) {
        this.removeRoleFromAllUsers(roleId);
        roleRepository.deleteById(roleId);
    }

    @Transactional
    @Override
    public ApplicationUser removeRoleFromUser(Long userId, Long roleId) {
        Optional<ApplicationUser> user = userRepository.findById(userId);
        Optional<Role> role = roleRepository.findById(roleId);
        if (role.isPresent() && role.get().getUsers().contains(user.get())){
            role.get().removeRoleFromUser(user.get());
            roleRepository.save(role.get());
            return user.get();
        }
        throw new UsernameNotFoundException("Role not found");
    }

    @Override
    public ApplicationUser assignRoleToUser(Long userId, Long roleId) {
        Optional<ApplicationUser> user = userRepository.findById(userId);
        Optional<Role> role = roleRepository.findById(roleId);
        if (user.isPresent() && user.get().getRoles().contains(role.get())) {
            throw new UserAlreadyExistsException(user.get().getFirstName() + " is already assigned " +
                    "to the role " + role.get().getName());
        }
        if (role.isPresent()) {
            role.get().assignRoleToUser(user.get());
            roleRepository.save(role.get());
        }
        return user.get();
    }

    @Override
    public Role removeRoleFromAllUsers(Long roleId) {
        Optional<Role> role = roleRepository.findById(roleId);
        role.get().removeRoleFromAllUsers();
        return roleRepository.save(role.get());
    }
}
