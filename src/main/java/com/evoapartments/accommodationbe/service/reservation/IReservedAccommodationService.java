package com.evoapartments.accommodationbe.service.reservation;

import com.evoapartments.accommodationbe.domain.reservation.ReservedAccommodation;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface IReservedAccommodationService {

    List<ReservedAccommodation> getAllAccommodationReservationsByAccommodationId(Long accommodationId);

    String saveReservation(Long accommodationId, ReservedAccommodation reservationRequest) throws IOException, GeneralSecurityException;

    List<ReservedAccommodation> getAllReservations();

    ReservedAccommodation findByReservationConfirmationCode(String confirmationCode);

    void cancelReservation(Long reservationId) throws IOException;

    List<ReservedAccommodation> getReservationsByUsername(String email);
}
