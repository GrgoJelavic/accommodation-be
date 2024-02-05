package com.evoapartments.accommodationbe.controller.user;

import com.evoapartments.accommodationbe.exception.RoleAlreadyExistsException;
import com.evoapartments.accommodationbe.domain.user.Role;
import com.evoapartments.accommodationbe.domain.user.ApplicationUser;
import com.evoapartments.accommodationbe.response.HttpResponse;
import com.evoapartments.accommodationbe.response.RoleResponse;
import com.evoapartments.accommodationbe.service.user.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/roles")
public class RoleController {
    private final IRoleService roleService;


    @GetMapping("/all-roles")
    public ResponseEntity<HttpResponse> getAllRoles(){
        List<Role> roles = roleService.getRoles();
        return roles.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("roles", roles))
                        .message("User role list fetched successfully.")
                        .status(HttpStatus.FOUND)
                        .statusCode(HttpStatus.FOUND.value())
                        .build());
    }

    @PostMapping("/create-new-role")
    public ResponseEntity<?> createNewRole(@RequestBody Role role){
        try {
            Role newRole = roleService.createRole(role);
            return ResponseEntity.created(URI.create("")).body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .data(Map.of("newRole", newRole))
                            .message("New role created successfully.")
                            .status(HttpStatus.CREATED)
                            .statusCode(HttpStatus.CREATED.value())
                            .build());
        } catch (RoleAlreadyExistsException ex){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @PutMapping("/update/{roleId}")
    public ResponseEntity<HttpResponse> updateUserRole(@PathVariable Long roleId,
                                                       @RequestParam(required = false) String name) {
        Role userRole = roleService.updateUserRole(roleId, name);
        RoleResponse roleResponse = getUserRoleResponse(userRole);
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("updatedRole", roleResponse))
                        .message("New user role created successfully.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @DeleteMapping("/delete/{roleId}")
    public ResponseEntity<HttpResponse> deleteRole(@PathVariable Long roleId){
        roleService.deleteRole(roleId);
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("The user role was deleted successfully.")
                        .status(HttpStatus.NO_CONTENT)
                        .statusCode(HttpStatus.NO_CONTENT.value())
                        .build());

    }

    @PostMapping("/remove-role-from-user")
    public ApplicationUser removeRoleFromUser(@RequestParam("userId") Long userId, @RequestParam("roleId") Long roleId){
        return roleService.removeRoleFromUser(userId, roleId);
    }

    @PostMapping("/assign-user-role")
    public ApplicationUser assignRoleToUser(@RequestParam("userId") Long userId, @RequestParam("roleId") Long roleId){
        return roleService.assignRoleToUser(userId, roleId);
    }

    private RoleResponse getUserRoleResponse(Role userRole) {
        return new RoleResponse(userRole.getId(), userRole.getName());
    }
}
