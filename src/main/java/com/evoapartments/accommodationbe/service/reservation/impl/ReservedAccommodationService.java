package com.evoapartments.accommodationbe.service.reservation;

import com.evoapartments.accommodationbe.exception.InvalidReservationRequestException;
import com.evoapartments.accommodationbe.exception.ResourceNotFoundException;
import com.evoapartments.accommodationbe.domain.accommodation.Accommodation;
import com.evoapartments.accommodationbe.domain.reservation.ReservedAccommodation;
import com.evoapartments.accommodationbe.repository.reservation.ReservedAccommodationRepository;
import com.evoapartments.accommodationbe.service.accommodation.IAccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservedAccommodationService implements IReservedAccommodationService {
    private final ReservedAccommodationRepository accommodationReservedRepository;
    private final IAccommodationService accommodationService;

    @Override
    public List<ReservedAccommodation> getAllReservations() {
        return accommodationReservedRepository.findAll();
    }

    public List<ReservedAccommodation> getAllAccommodationReservationsByAccommodationId(Long accommodationId){
        return accommodationReservedRepository.findByAccommodationId(accommodationId);
    }

    @Override
    public String saveReservation(Long accommodationId, ReservedAccommodation reservationRequest) {
        if(reservationRequest.getCheckOutDate().isBefore(reservationRequest.getCheckInDate())){
            throw  new InvalidReservationRequestException("Check-in date must be before Check-out date");
        }
        Accommodation accommodation = accommodationService.getAccommodationById(accommodationId).get();
        List<ReservedAccommodation> existingReservations = accommodation.getReservations();

        boolean accommodationIsAvailable = accommodationIsAvailable(reservationRequest, existingReservations);
        if (accommodationIsAvailable){
            accommodation.addReservation(reservationRequest);
            accommodationReservedRepository.save(reservationRequest);
        } else {
            throw new InvalidReservationRequestException("The accommodation has been booked for the selected dates.");
        }
        return reservationRequest.getReservationConfirmationCode();
    }

    @Override
    public ReservedAccommodation findByReservationConfirmationCode(String confirmationCode) {
        return  accommodationReservedRepository.findByReservationConfirmationCode(confirmationCode)
                        .orElseThrow(() -> new ResourceNotFoundException("No reservation found with confirmation code :"+confirmationCode));
    }

    @Override
    public void cancelReservation(Long reservationId) {
        accommodationReservedRepository.deleteById(reservationId);
    }

    @Override
    public List<ReservedAccommodation> getReservationsByUsername(String email) {
        return accommodationReservedRepository.findReservationsByGuestEmail(email);
    }

    private boolean accommodationIsAvailable(ReservedAccommodation reservationRequest, List<ReservedAccommodation> existingReservations) {
        return existingReservations.stream()
                .noneMatch(existingReservation ->
                        reservationRequest.getCheckInDate().equals(existingReservation.getCheckInDate())
                                || reservationRequest.getCheckOutDate().isBefore(existingReservation.getCheckOutDate())
                                || (reservationRequest.getCheckInDate().isAfter(existingReservation.getCheckInDate())
                                && reservationRequest.getCheckInDate().isBefore(existingReservation.getCheckOutDate()))
                                || (reservationRequest.getCheckInDate().isBefore(existingReservation.getCheckInDate())

                                && reservationRequest.getCheckOutDate().equals(existingReservation.getCheckOutDate()))
                                || (reservationRequest.getCheckInDate().isBefore(existingReservation.getCheckInDate())

                                && reservationRequest.getCheckOutDate().isAfter(existingReservation.getCheckOutDate()))

                                || (reservationRequest.getCheckInDate().equals(existingReservation.getCheckOutDate())
                                && reservationRequest.getCheckOutDate().equals(existingReservation.getCheckInDate()))

                                || (reservationRequest.getCheckInDate().equals(existingReservation.getCheckOutDate())
                                && reservationRequest.getCheckOutDate().equals(reservationRequest.getCheckInDate()))
                );
    }
}
