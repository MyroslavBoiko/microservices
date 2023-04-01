package com.microservices.songservice.exception;

public class DuplicateResourceIdException extends RuntimeException {
    public DuplicateResourceIdException(String message) {
        super(message);
    }
}
