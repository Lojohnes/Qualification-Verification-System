package com.aqvp.platform.identity.exception;

/**
 * Exception thrown when a chosen username is already registered.
 */
public class UsernameAlreadyExistsException extends UserAlreadyExistsException {

    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
