package com.aqvp.platform.qualification.exception;

/**
 * Exception thrown when a specific entity is not found.
 */
public class EntityNotFoundException extends ResourceNotFoundException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
