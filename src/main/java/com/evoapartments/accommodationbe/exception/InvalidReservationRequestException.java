package com.evoapartments.accommodationbe.exception;

public class InvalidReservationRequestException extends RuntimeException {
    public InvalidReservationRequestException(String message){
        super(message);
    }
}
