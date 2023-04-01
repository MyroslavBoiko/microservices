package com.microservices.resource.exception;

public class RangeValidationException extends RuntimeException {
    public RangeValidationException(String message) {
        super(message);
    }
}
