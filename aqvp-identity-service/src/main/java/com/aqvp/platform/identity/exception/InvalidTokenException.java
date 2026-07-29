package com.aqvp.platform.identity.exception;

/**
 * Exception thrown when a JWT or refresh token is invalid.
 */
public class InvalidTokenException extends BusinessException {

    public InvalidTokenException(String message) {
        super(message);
    }
}
