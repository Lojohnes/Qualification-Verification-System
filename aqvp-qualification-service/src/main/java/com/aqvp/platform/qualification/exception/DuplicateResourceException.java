package com.aqvp.platform.qualification.exception;

/**
 * Exception thrown when a resource already exists (e.g., duplicate code).
 */
public class DuplicateResourceException extends BusinessException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
