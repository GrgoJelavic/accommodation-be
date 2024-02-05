package com.evoapartments.accommodationbe.resource.user;

import com.evoapartments.accommodationbe.exception.RoleAlreadyExistsException;
import com.evoapartments.accommodationbe.domain.user.Role;
import com.evoapartments.accommodationbe.domain.user.ApplicationUser;
import com.evoapartments.accommodationbe.response.RoleResponse;
import com.evoapartments.accommodationbe.service.user.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/roles")
public class RoleController {

    private final IRoleService roleService;

    @GetMapping("/all-roles")
    public ResponseEntity<List<Role>> getAllRoles(){
        return new ResponseEntity<>(roleService.getRoles(), HttpStatus.FOUND);
    }

    @PostMapping("/create-new-role")
    public ResponseEntity<String> createNewRole(@RequestBody Role role){
        try {
            roleService.createRole(role);
            return ResponseEntity.ok("New role created successfully");
        } catch (RoleAlreadyExistsException ex){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @PutMapping("/update/{roleId}")
    public ResponseEntity<RoleResponse> updateUserRole(@PathVariable Long roleId,
                                                       @RequestParam(required = false) String name) {
        Role userRole = roleService.updateUserRole(roleId, name);
        RoleResponse roleResponse = getUserRoleResponse(userRole);
        return ResponseEntity.ok(roleResponse);
    }

    @DeleteMapping("/delete/{roleId}")
    public void deleteRole(@PathVariable Long roleId){
        roleService.deleteRole(roleId);
    }

    @PostMapping("/remove-role-from-user")
    public ApplicationUser removeRoleFromUser(@RequestParam("userId") Long userId, @RequestParam("roleId") Long roleId){
        return roleService.removeRoleFromUser(userId, roleId);
    }

    @PostMapping("/assign-user-role")
    public ApplicationUser assignRoleToUser(@RequestParam("userId") Long userId, @RequestParam("roleId") Long roleId){
        return roleService.assignRoleToUser(userId, roleId);
    };

    private RoleResponse getUserRoleResponse(Role userRole) {
        return new RoleResponse(userRole.getId(), userRole.getName());
    }
}
