package com.evoapartments.accommodationbe.controller.user;

import com.evoapartments.accommodationbe.exception.UserAlreadyExistsException;
import com.evoapartments.accommodationbe.domain.user.ApplicationUser;
import com.evoapartments.accommodationbe.request.LoginRequest;
import com.evoapartments.accommodationbe.response.HttpResponse;
import com.evoapartments.accommodationbe.response.JwtResponse;
import com.evoapartments.accommodationbe.security.jwt.JwtUtils;
import com.evoapartments.accommodationbe.security.user.ApplicationUserDetails;
import com.evoapartments.accommodationbe.service.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@RequestBody ApplicationUser user){
//        Todo: validation that we are receiving good data from user
        try {
            ApplicationUser newUser = userService.saveUser(user);
            return ResponseEntity.created(URI.create("")).body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .data(Map.of("user", newUser))
                            .message("User registered successfully")
                            .status(HttpStatus.CREATED)
                            .statusCode(HttpStatus.CREATED.value())
                            .build());
        } catch (UserAlreadyExistsException ex){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        Authentication authenticationRequest = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticationRequest);
        String jwtToken = jwtUtils.generateJwtTokenForUser(authenticationRequest);
        ApplicationUserDetails userDetails = (ApplicationUserDetails) authenticationRequest.getPrincipal();
        List<String> userRoles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(
                userDetails.getId(),
                userDetails.getEmail(),
                jwtToken,
                userRoles));
    }

    @GetMapping("/verification")
    public ResponseEntity<HttpResponse> confirmNewUserAccount(@RequestParam("token") String token){
            Boolean isSuccess = userService.verifyToken(token);
            return ResponseEntity.created(URI.create("")).body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .data(Map.of("Success", isSuccess))
                            .message("User account verified successfully.")
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .build());
    }}
