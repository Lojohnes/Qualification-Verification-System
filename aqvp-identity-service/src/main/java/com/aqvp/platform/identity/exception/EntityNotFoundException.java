package com.aqvp.platform.identity.exception;

/**
 * Exception thrown when a requested entity cannot be found.
 */
public class EntityNotFoundException extends ResourceNotFoundException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
