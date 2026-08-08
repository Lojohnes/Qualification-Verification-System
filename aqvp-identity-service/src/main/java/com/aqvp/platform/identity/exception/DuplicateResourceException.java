package com.aqvp.platform.identity.exception;

/**
 * Exception thrown when a resource already exists and duplicates are not allowed.
 */
public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
