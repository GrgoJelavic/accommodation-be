package com.evoapartments.accommodationbe.service.user;

import com.evoapartments.accommodationbe.domain.mail.EmailConfirmation;
import com.evoapartments.accommodationbe.exception.ResourceNotFoundException;
import com.evoapartments.accommodationbe.exception.UserAlreadyExistsException;
import com.evoapartments.accommodationbe.domain.user.Role;
import com.evoapartments.accommodationbe.domain.user.ApplicationUser;
import com.evoapartments.accommodationbe.repository.mail.EmailConfirmationRepository;
import com.evoapartments.accommodationbe.repository.user.RoleRepository;
import com.evoapartments.accommodationbe.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.module.Configuration;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailConfirmationRepository confirmationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<ApplicationUser> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public ApplicationUser getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User email not found"));
    }

    @Override
    public Boolean verifyToken(String token) {
        return null;
    }

    @Override
    public ApplicationUser saveUser(ApplicationUser user) {
        if(userRepository.existsByEmail(user.getEmail())){
            throw new UserAlreadyExistsException(user.getEmail() + " already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false);

        EmailConfirmation confirmation = new EmailConfirmation(user);
        confirmationRepository.save(confirmation);

//        TODO: Send email to user with token;

        Role userRole = roleRepository.findRoleByName("ROLE_USER").get();
        user.setRoles(Collections.singletonList(userRole));

        return userRepository.save(user);
    }

    @Override
    public ApplicationUser updateUser(Long userId, String firstName, String lastName, String email, String password) {
        ApplicationUser user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found!"));
        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (email != null) user.setEmail(email);
        if (password != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(String email) {
        ApplicationUser user = getUserByEmail(email);
        if (user != null){
            userRepository.deleteByEmail(email);
        }
        userRepository.deleteByEmail(email);
    }
}
