package com.evoapartments.accommodationbe.controller.user;

import com.evoapartments.accommodationbe.domain.user.ApplicationUser;
import com.evoapartments.accommodationbe.response.ApplicationUserResponse;
import com.evoapartments.accommodationbe.response.HttpResponse;
import com.evoapartments.accommodationbe.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final IUserService userService;

    @GetMapping("/all-users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<HttpResponse> getAllUsers(){
        List<ApplicationUser> users = userService.getAllUsers();
            return ResponseEntity.created(URI.create("")).body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .data(Map.of("users", users, "size", users.size()))
                            .message("Users list fetched successfully.")
                            .status(HttpStatus.FOUND)
                            .statusCode(HttpStatus.FOUND.value())
                            .build());
    }

    @GetMapping("/user/{email}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<HttpResponse> getUserByEmail(@PathVariable("email") String email){
        ApplicationUser user = userService.getUserByEmail(email);
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", user))
                        .message("User found successfully.")
                        .status(HttpStatus.FOUND)
                        .statusCode(HttpStatus.FOUND.value())
                        .build());
    }

    @PutMapping("/update/user/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #email == principal.username)")
    public ResponseEntity<HttpResponse> updateUser(@PathVariable Long userId,
                                                              @RequestParam(required = false) String firstName,
                                                              @RequestParam(required = false) String lastName,
                                                              @RequestParam(required = false) String email,
                                                              @RequestParam(required = false) String password) {
        ApplicationUser user = userService.updateUser(userId, firstName, lastName, email, password);
        ApplicationUserResponse userResponse = getAppUserResponse(user);
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userResponse))
                        .message("User updated successfully.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }


    @DeleteMapping("/delete/user/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #email == principal.username)")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable String email){
        userService.deleteUser(email);
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("User has been deleted successfully.")
                        .status(HttpStatus.NO_CONTENT)
                        .statusCode(HttpStatus.NO_CONTENT.value())
                        .build());
    }

    private ApplicationUserResponse getAppUserResponse(ApplicationUser user) {
        return new ApplicationUserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }
}
