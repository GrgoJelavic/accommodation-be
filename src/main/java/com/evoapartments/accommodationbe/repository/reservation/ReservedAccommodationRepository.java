package com.evoapartments.accommodationbe.repository.reservation;

import com.evoapartments.accommodationbe.domain.reservation.ReservedAccommodation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservedAccommodationRepository extends JpaRepository<ReservedAccommodation, Long> {
    Optional<ReservedAccommodation>  findByReservationConfirmationCode(String confirmationCode);
    List<ReservedAccommodation> findByAccommodationId(Long accommodationId);
    List<ReservedAccommodation> findReservationsByGuestEmail(String email);
}
