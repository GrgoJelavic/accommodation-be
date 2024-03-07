package com.evoapartments.accommodationbe.service.reservation.impl;

import com.evoapartments.accommodationbe.exception.InvalidReservationRequestException;
import com.evoapartments.accommodationbe.exception.ResourceNotFoundException;
import com.evoapartments.accommodationbe.domain.accommodation.Accommodation;
import com.evoapartments.accommodationbe.domain.reservation.ReservedAccommodation;
import com.evoapartments.accommodationbe.repository.reservation.ReservedAccommodationRepository;
import com.evoapartments.accommodationbe.service.accommodation.IAccommodationService;
import com.evoapartments.accommodationbe.service.calendar.ICalendarSyncService;
import com.evoapartments.accommodationbe.service.calendar.IGoogleCalendarService;
import com.evoapartments.accommodationbe.service.reservation.IReservedAccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservedAccommodationService implements IReservedAccommodationService {
    private final ReservedAccommodationRepository accommodationReservedRepository;
    private final IAccommodationService accommodationService;
    private final IGoogleCalendarService googleCalendarService;
    private final ICalendarSyncService calendarSyncService;

    @Override
    public List<ReservedAccommodation> getAllReservations() {
        return accommodationReservedRepository.findAll();
    }

    public List<ReservedAccommodation> getAllAccommodationReservationsByAccommodationId(Long accommodationId){
        return accommodationReservedRepository.findByAccommodationId(accommodationId);
    }

    @Override
    public String saveReservation(Long accommodationId, ReservedAccommodation request) throws IOException, GeneralSecurityException {
        if (request.getCheckOutDate().isBefore(request.getCheckInDate())) {
            throw new InvalidReservationRequestException("Check-in date must be before Check-out date");
        }
        Accommodation accommodation = accommodationService.getAccommodationById(accommodationId).orElse(null);
        if (accommodation != null) {
            String calendarId = calendarSyncService.getGoogleCalendarIdExportByAccommodationId(accommodationId);
            if (googleCalendarService.getAccommodationAvailabilityFromCalendarsFreeBusy(request.getCheckInDate().toString(),
                    request.getCheckOutDate().toString(), accommodationId)) {
                String eventId = googleCalendarService.scheduleReservationEventToGoogleCalendar(accommodationId, calendarId, request);
                accommodation.addReservation(request, eventId);
                accommodationReservedRepository.save(request);
            } else {
                throw new InvalidReservationRequestException("The accommodation has been booked for the selected dates.");
            }
        } else {
            throw new InvalidReservationRequestException("The accommodation data fetch unsuccessful.");
        }
        return request.getReservationConfirmationCode();
    }

    @Override
    public ReservedAccommodation findByReservationConfirmationCode(String confirmationCode) {
        return  accommodationReservedRepository.findByReservationConfirmationCode(confirmationCode).orElseThrow(
                () -> new ResourceNotFoundException("There is no any reservation found with confirmation code: " + confirmationCode));
    }

    @Override
    public void cancelReservation(Long reservationId) throws IOException {
        ReservedAccommodation reservation = accommodationReservedRepository.findById(reservationId).orElse(null);
        if (reservation != null) {
            String calendarId = calendarSyncService.getGoogleCalendarIdExportByAccommodationId(reservation.getAccommodation().getId());
            googleCalendarService.deleteGoogleCalendarReservationEvent(calendarId, reservation.getCalendarEventId());
            accommodationReservedRepository.deleteById(reservationId);
        }
    }

    @Override
    public List<ReservedAccommodation> getReservationsByUsername(String email) {
        return accommodationReservedRepository.findReservationsByGuestEmail(email);
    }
}
