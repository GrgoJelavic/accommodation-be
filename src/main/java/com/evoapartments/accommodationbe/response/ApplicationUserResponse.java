package com.evoapartments.accommodationbe.response;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class ApplicationUserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public ApplicationUserResponse(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
