package com.aqvp.platform.identity.exception;

/**
 * Exception thrown when a refresh token cannot be processed.
 */
public class TokenRefreshException extends BusinessException {

    public TokenRefreshException(String message) {
        super(message);
    }
}
