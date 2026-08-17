package com.aqvp.platform.identity.exception;

/**
 * Thrown when public self-registration is attempted after the system has already
 * been initialized with at least one account.
 */
public class RegistrationDisabledException extends BusinessException {

    public RegistrationDisabledException(String message) {
        super(message);
    }
}
