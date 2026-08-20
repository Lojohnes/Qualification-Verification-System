package com.aqvp.platform.verification.exception;

/**
 * Exception thrown when a required upstream service call fails.
 */
public class UpstreamServiceException extends RuntimeException {

    public UpstreamServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
