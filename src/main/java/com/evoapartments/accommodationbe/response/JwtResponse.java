package com.evoapartments.accommodationbe.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
public class JwtResponse {
    private Long id;
    private String email;
    private String token;
    private String type = "Bearer";
    private List<String> roles;

    public JwtResponse(Long id, String email, String jwtToken, List<String> userRoles) {
        this.id = id;
        this.email = email;
        this.token = jwtToken;
        this.roles = userRoles;
    }
}
