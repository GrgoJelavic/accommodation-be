package com.evoapartments.accommodationbe.service.user.impl;

import com.evoapartments.accommodationbe.domain.email.EmailConfirmation;
import com.evoapartments.accommodationbe.exception.ResourceNotFoundException;
import com.evoapartments.accommodationbe.exception.UserAlreadyExistsException;
import com.evoapartments.accommodationbe.domain.user.Role;
import com.evoapartments.accommodationbe.domain.user.ApplicationUser;
import com.evoapartments.accommodationbe.repository.mail.EmailConfirmationRepository;
import com.evoapartments.accommodationbe.repository.user.RoleRepository;
import com.evoapartments.accommodationbe.repository.user.UserRepository;
import com.evoapartments.accommodationbe.service.email.IEmailService;
import com.evoapartments.accommodationbe.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailConfirmationRepository confirmationRepository;
    private final PasswordEncoder passwordEncoder;
    private final IEmailService emailService;

    @Override
    public List<ApplicationUser> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public ApplicationUser getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found."));
    }

    @Override
    public ApplicationUser saveUser(ApplicationUser user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException(user.getEmail() + " already exists.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false);
        userRepository.save(user);

        EmailConfirmation confirmation = new EmailConfirmation(user);
        confirmationRepository.save(confirmation);
        emailService.sendHtmlEmailWithEmbeddedFiles(user.getFirstName(), user.getEmail(), confirmation.getToken());

        Role userRole = roleRepository.findRoleByName("ROLE_USER").orElse(null);
        user.setRoles(Collections.singletonList(userRole));

        return userRepository.save(user);
    }

    @Override
    public Boolean verifyToken(String token) {
        EmailConfirmation confirmation = confirmationRepository.findByToken(token);
        ApplicationUser user = userRepository.findByEmailIgnoreCase(confirmation.getUser().getEmail());
        user.setEnabled(true);
        userRepository.save(user);
        return Boolean.TRUE;
    }

    @Override
    public ApplicationUser updateUser(Long userId, String firstName, String lastName, String email, String password) {
        ApplicationUser user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found!"));
        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (email != null) user.setEmail(email);
        if (password != null) user.setPassword(passwordEncoder.encode(user.getPassword()));
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
