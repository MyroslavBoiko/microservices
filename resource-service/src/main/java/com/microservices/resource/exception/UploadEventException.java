package com.microservices.resource.exception;

public class UploadEventException extends RuntimeException {
    public UploadEventException(String message) {
        super(message);
    }
}
