package com.aqvp.platform.verification.exception;

/**
 * Exception thrown when Qualification service has no matching authoritative record.
 */
public class UpstreamNotFoundException extends RuntimeException {

    public UpstreamNotFoundException(String message) {
        super(message);
    }
}
