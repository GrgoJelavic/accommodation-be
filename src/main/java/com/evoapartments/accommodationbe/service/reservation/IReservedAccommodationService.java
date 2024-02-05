package com.evoapartments.accommodationbe.service;

import com.evoapartments.accommodationbe.model.reservation.ReservedAccommodation;

import java.util.List;

public interface IAccommodationReservedService {
    List<ReservedAccommodation> getAllAccommodationReservationsByAccommodationId(Long accommodationId);

    String saveReservation(Long accommodationId, ReservedAccommodation reservationRequest);

    List<ReservedAccommodation> getAllReservations();

    ReservedAccommodation findByReservationConfirmationCode(String confirmationCode);

    void cancelReservation(Long reservationId);

    List<ReservedAccommodation> getReservationsByUsername(String email);
}
