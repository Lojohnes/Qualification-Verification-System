package com.aqvp.platform.identity.exception;

/**
 * Exception thrown when an API client account is disabled.
 */
public class ApiClientDisabledException extends ApiClientAuthenticationException {

    public ApiClientDisabledException(String message) {
        super(message);
    }
}
