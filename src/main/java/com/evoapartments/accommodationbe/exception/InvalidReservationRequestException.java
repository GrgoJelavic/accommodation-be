package com.evoapartments.accommodationbe.exception;

public class InvalidReservationResponseException extends RuntimeException {
    public InvalidReservationRequestException(String message){
        super(message);
    }
}
