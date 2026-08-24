package com.aqvp.platform.verification.exception;

/**
 * Exception thrown when a requested verification resource does not exist.
 */
public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
