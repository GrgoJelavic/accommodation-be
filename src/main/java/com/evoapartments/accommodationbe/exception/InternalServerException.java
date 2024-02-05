package com.evoapartments.accommodationbe.exception;

public class InternalServerException extends RuntimeException {
    public InternalServerException(String message) {
        super(message);
    }
}
