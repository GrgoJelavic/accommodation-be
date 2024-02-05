package com.evoapartments.accommodationbe.resource.user;

import com.evoapartments.accommodationbe.domain.user.ApplicationUser;
import com.evoapartments.accommodationbe.response.ApplicationUserResponse;
import com.evoapartments.accommodationbe.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all-users")
    public ResponseEntity<List<ApplicationUser>> getUsers(){
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.FOUND);
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable("email") String email){
        try {
            ApplicationUser theUser = userService.getUserByEmail(email);
            return ResponseEntity.ok(theUser);
        } catch (UsernameNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching user");
        }
    }

    @PutMapping("/update/user/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApplicationUserResponse> updateUser(@PathVariable Long userId,
                                                              @RequestParam(required = false) String firstName,
                                                              @RequestParam(required = false) String lastName,
                                                              @RequestParam(required = false) String email,
                                                              @RequestParam(required = false) String password) {
        // TODO: fix - unable to login after password update
        ApplicationUser user = userService.updateUser(userId, firstName, lastName, email, password);
        ApplicationUserResponse userResponse = getAppUserResponse(user);
        return ResponseEntity.ok(userResponse);
    }

    @DeleteMapping("/delete/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #email == principal.username)")
    public ResponseEntity<String> deleteUser(@PathVariable String email){
        try {
            userService.deleteUser(email);
            return ResponseEntity.ok("User is deleted successfully");
        } catch (UsernameNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting the user");
        }
    }

    private ApplicationUserResponse getAppUserResponse(ApplicationUser user) {
        return new ApplicationUserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }
}
