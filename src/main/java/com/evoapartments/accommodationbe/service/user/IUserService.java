package com.evoapartments.accommodationbe.service;

import com.evoapartments.accommodationbe.model.user.ApplicationUser;

import java.util.List;

public interface IUserService {

    ApplicationUser registerUser(ApplicationUser user);

    List<ApplicationUser> getAllUsers();

    void deleteUser(String email);

    ApplicationUser getUserByEmail(String email);
}
