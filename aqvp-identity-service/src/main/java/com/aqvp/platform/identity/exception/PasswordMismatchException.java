package com.aqvp.platform.identity.exception;

/**
 * Exception thrown when password confirmation does not match.
 */
public class PasswordMismatchException extends BusinessException {

    public PasswordMismatchException(String message) {
        super(message);
    }
}
