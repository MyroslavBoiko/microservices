package com.microservices.resource.exception;

public class JsonConvertException extends RuntimeException {
    public JsonConvertException(String message) {
        super(message);
    }
}
