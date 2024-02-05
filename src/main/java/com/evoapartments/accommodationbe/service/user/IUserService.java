package com.evoapartments.accommodationbe.service.user;

import com.evoapartments.accommodationbe.domain.user.ApplicationUser;

import java.util.List;

public interface IUserService {

    List<ApplicationUser> getAllUsers();

    ApplicationUser getUserByEmail(String email);

    Boolean verifyToken(String token);

    ApplicationUser saveUser(ApplicationUser user);

    ApplicationUser updateUser(Long userId, String firstName, String lastName, String email, String password);

    void deleteUser(String email);
}
