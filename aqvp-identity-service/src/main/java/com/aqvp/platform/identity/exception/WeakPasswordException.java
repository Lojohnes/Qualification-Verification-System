package com.aqvp.platform.identity.exception;

/**
 * Exception thrown when a password does not meet the strength requirements.
 */
public class WeakPasswordException extends BusinessException {

    public WeakPasswordException(String message) {
        super(message);
    }
}
