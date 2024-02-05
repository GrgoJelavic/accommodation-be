package com.evoapartments.accommodationbe.security.user;

import com.evoapartments.accommodationbe.domain.user.ApplicationUser;
import com.evoapartments.accommodationbe.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        ApplicationUser user = userRepository.findByEmail(userEmail).orElseThrow(
                ()-> new UsernameNotFoundException("User not found"));
        return ApplicationUserDetails.buildUserDetails(user);
    }
}
