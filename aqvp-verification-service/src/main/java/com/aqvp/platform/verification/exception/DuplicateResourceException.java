package com.aqvp.platform.verification.exception;

/**
 * Exception thrown when a unique verification resource already exists.
 */
public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
