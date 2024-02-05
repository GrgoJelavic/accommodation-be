package com.evoapartments.accommodationbe.repository.mail;

import com.evoapartments.accommodationbe.domain.email.EmailConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailConfirmationRepository extends JpaRepository<EmailConfirmation, Long> {
    EmailConfirmation findByToken(String token);
}
