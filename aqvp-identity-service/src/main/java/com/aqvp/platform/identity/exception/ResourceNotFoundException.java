package com.aqvp.platform.identity.exception;

/**
 * Exception thrown when a requested resource cannot be found.
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
