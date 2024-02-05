package com.evoapartments.accommodationbe.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private Collection<ApplicationUser> users = new HashSet<>();

    public Role(String name) {
        this.name = name;
    }

    public void assignRoleToUser(ApplicationUser user){
        user.getRoles().add(this);
        this.getUsers().add(user);
    }

    public void removeRoleFromUser(ApplicationUser user){
        user.getRoles().remove(this);
        this.getUsers().remove(user);
    }

    public void removeRoleFromAllUsers(){
        if(this.getUsers() != null){
            List<ApplicationUser> roleUsers = this.getUsers().stream().toList();
            roleUsers.forEach(this ::removeRoleFromUser);
        }
    }

    public String getName(){
        return name != null ? name : "";
    }
}
