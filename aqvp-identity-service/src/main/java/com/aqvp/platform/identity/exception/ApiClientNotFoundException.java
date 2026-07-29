package com.aqvp.platform.identity.exception;

/**
 * Exception thrown when an API client cannot be found.
 */
public class ApiClientNotFoundException extends ResourceNotFoundException {

    public ApiClientNotFoundException(String message) {
        super(message);
    }
}
