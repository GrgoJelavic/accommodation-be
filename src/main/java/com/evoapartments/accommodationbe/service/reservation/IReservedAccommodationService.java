package com.evoapartments.accommodationbe.service.reservation;

import com.evoapartments.accommodationbe.domain.reservation.ReservedAccommodation;

import java.util.List;

public interface IReservedAccommodationService {

    List<ReservedAccommodation> getAllAccommodationReservationsByAccommodationId(Long accommodationId);

    String saveReservation(Long accommodationId, ReservedAccommodation reservationRequest);

    List<ReservedAccommodation> getAllReservations();

    ReservedAccommodation findByReservationConfirmationCode(String confirmationCode);

    void cancelReservation(Long reservationId);

    List<ReservedAccommodation> getReservationsByUsername(String email);
}
