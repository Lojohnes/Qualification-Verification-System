package com.aqvp.platform.identity.exception;

/**
 * Exception thrown when API client authentication fails.
 */
public class ApiClientAuthenticationException extends BusinessException {

    public ApiClientAuthenticationException(String message) {
        super(message);
    }
}
