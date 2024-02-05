package com.evoapartments.accommodationbe.response;

import com.evoapartments.accommodationbe.domain.user.ApplicationUser;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashSet;

@Data
@NoArgsConstructor
public class RoleResponse {
    private Long id;
    private String name;
    private Collection<ApplicationUser> users = new HashSet<>();

    public RoleResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public RoleResponse(Long id, String name, Collection<ApplicationUser> users) {
        this.id = id;
        this.name = name;
        this.users = users;
    }
}
