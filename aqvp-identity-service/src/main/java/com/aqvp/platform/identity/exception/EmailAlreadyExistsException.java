package com.aqvp.platform.identity.exception;

/**
 * Exception thrown when an email is already registered.
 */
public class EmailAlreadyExistsException extends DuplicateResourceException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
