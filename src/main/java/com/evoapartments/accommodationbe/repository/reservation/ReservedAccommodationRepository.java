package com.evoapartments.accommodationbe.repository;

import com.evoapartments.accommodationbe.model.reservation.ReservedAccommodation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ReservedAccommodationRepository extends JpaRepository<ReservedAccommodation, Long> {
    Optional<ReservedAccommodation>  findByReservationConfirmationCode(String confirmationCode);

    List<ReservedAccommodation> findByAccommodationId(Long accommodationId);

//    @Query("SELECT ar from AccommodationReserved ar WHERE ar.guestEmail = ':email'")
    List<ReservedAccommodation> findReservationsByGuestEmail(String email);
}
